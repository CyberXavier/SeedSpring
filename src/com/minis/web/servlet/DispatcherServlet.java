package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.web.context.support.AnnotationConfigWebApplicationContext;
import com.minis.web.context.WebApplicationContext;
import com.minis.web.context.support.XmlScanComponentHelper;
import com.minis.web.method.HandlerMethod;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
    public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";
    public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";
    public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";

    private WebApplicationContext webApplicationContext;
    private WebApplicationContext parentApplicationContext;
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;

    private ViewResolver viewResolver;
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
        initViewResolvers(this.webApplicationContext);
    }

    private void initHandlerAdapters(WebApplicationContext wac) {
        try {
            this.handlerAdapter = (HandlerAdapter) wac.getBean(HANDLER_ADAPTER_BEAN_NAME);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    private void initHandlerMappings(WebApplicationContext wac) {
        try {
            this.handlerMapping = (HandlerMapping) wac.getBean(HANDLER_MAPPING_BEAN_NAME);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initViewResolvers(WebApplicationContext wac) {
        try {
            this.viewResolver = (ViewResolver) wac.getBean(VIEW_RESOLVER_BEAN_NAME);
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    private void initController() {
        // 从MVC管理的容器中获取controllerName (com.test.controller.HelloWorldBean)
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

    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerMethod handlerMethod = null;
        ModelAndView mv = null;

        handlerMethod = this.handlerMapping.getHandler(processedRequest);
        if (handlerMethod == null) {
            return;
        }

        HandlerAdapter ha = this.handlerAdapter;
        mv = ha.handle(processedRequest, response, handlerMethod);
        render(processedRequest, response, mv);
    }

    private void render(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception {
        // responseBody或者void两种情况
        if (mv == null) {
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }

        String sTarget = mv.getViewName();
        Map<String, Object> modelMap = mv.getModel();
        View view = resolveViewName(sTarget, modelMap, request);
        view.render(modelMap, request, response);

    }

    private View resolveViewName(String viewName, Map<String, Object> modelMap, HttpServletRequest request) throws Exception {
        if (this.viewResolver != null) {
            View view = viewResolver.resolveViewName(viewName);
            if (view != null) {
                return view;
            }
        }
        return null;
    }
}
