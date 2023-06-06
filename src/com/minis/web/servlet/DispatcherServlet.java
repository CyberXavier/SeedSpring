package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.AnnotationConfigWebApplicationContext;
import com.minis.web.RequestMapping;
import com.minis.web.WebApplicationContext;
import com.minis.web.XmlScanComponentHelper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";

    private WebApplicationContext webApplicationContext;
    private WebApplicationContext parentApplicationContext;
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;
    private String sContextConfigLocation;
    // 用来存储需要扫描的package
    private List<String> packageNames = new ArrayList<>();
    // 用来存储controller的名字和对象的映射关系
    private Map<String, Object> controllerObjs = new HashMap<>();
    // 用于存储controller名称数组列表
    private List<String> controllerNames = new ArrayList<>();
    // 用于存储controller名称与类的映射关系
    private Map<String,Class<?>> controllerClasses = new HashMap<>();
    // 保存自定义的 @RequestMapping 名称 （URL的名称） 的列表
    private List<String> urlMappingNames = new ArrayList<>();
    // 保存URl名称与对象的映射关系
    private Map<String,Object> mappingObjs = new HashMap<>();
    // 保存URL名称与方法的映射关系
    private Map<String,Method> mappingMethods = new HashMap<>();

    public DispatcherServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.parentApplicationContext = (WebApplicationContext) this.getServletContext()
                .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmlPath = null;
        try {
            xmlPath = this.getServletContext().getResource(sContextConfigLocation);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);
        this.webApplicationContext = new AnnotationConfigWebApplicationContext(sContextConfigLocation, this.parentApplicationContext);
        Refresh();
    }

    //对所有的mappingValues中注册的类进行实例化，默认构造函数
    private void Refresh() {
        initController(); // 初始化 controller

        initHandlerMappings(this.webApplicationContext);
        initHandlerAdapters(this.webApplicationContext);
    }

    private void initHandlerAdapters(WebApplicationContext webApplicationContext) {
        this.handlerMapping = new RequestMappingHandlerMapping(webApplicationContext);
    }

    private void initHandlerMappings(WebApplicationContext webApplicationContext) {
        this.handlerAdapter = new RequestMappingHandlerAdapter(webApplicationContext);
    }


    private void initController() {
        this.controllerNames = Arrays.asList(this.webApplicationContext.getBeanDefinitionNames());
        for (String controllerName : this.controllerNames) {
            try {
                this.controllerClasses.put(controllerName, Class.forName(controllerName));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                this.controllerObjs.put(controllerName, this.webApplicationContext.getBean(controllerName));
                System.out.println("controller : "+controllerName);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<String> scanPackages(List<String> packageNames) {
        List<String> tempControllerNames = new ArrayList<String>();
        for (String packageName : packageNames) {
            tempControllerNames.addAll(scanPackage(packageName));
        }
        return tempControllerNames;
    }

    private List<String> scanPackage(String packageName) {
        List<String> tempControllerNames = new ArrayList<>();
        URI uri = null;
        //将以.分隔的包名换成以/分隔的uri
        try {
            uri = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/")).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        File dir = new File(uri);
        //处理对应的文件目录
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) { //对子目录递归扫描
                scanPackage(packageName+"."+file.getName());
            }else { //类文件
                String controllerName = packageName + "."
                        + file.getName().replace(".class","");
                tempControllerNames.add(controllerName);
            }
        }
        return tempControllerNames;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.webApplicationContext);
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpServletRequest processedRequest = req;
        HandlerMethod handlerMethod = null;
        handlerMethod = this.handlerMapping.getHandler(processedRequest);
        if (handlerMethod == null) {
            return;
        }
        HandlerAdapter ha = this.handlerAdapter;
        ha.handle(processedRequest, resp, handlerMethod);
    }
}
