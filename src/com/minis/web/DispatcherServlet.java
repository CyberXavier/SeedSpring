package com.minis.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
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

    public DispatcherServlet () {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmlPath = null;
        try {
            xmlPath = this.getServletContext().getResource(sContextConfigLocation);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);

        Refresh();
    }

    //对所有的mappingValues中注册的类进行实例化，默认构造函数
    private void Refresh() {
        initController(); // 初始化 controller
        initMapping(); // 初始化 url 映射
    }

    private void initController() {
        // 扫描包，获取所有类名
        this.controllerNames = scanPackages(this.packageNames);
        for (String controllerName : this.controllerNames) {
            Object obj = null;
            Class<?> clz = null;
            try {
                clz = Class.forName(controllerName); //加载类
                this.controllerClasses.put(controllerName, clz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                obj = clz.newInstance(); //实例化bean
                this.controllerObjs.put(controllerName, obj);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
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

    private void initMapping() {
        for(String controllerName : this.controllerNames) {
            Class<?> clazz = this.controllerClasses.get(controllerName);
            Object obj = this.controllerObjs.get(controllerName);
            Method[] methods = clazz.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {
                    //检查所有的方法
                    boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                    if (isRequestMapping) { //有RequestMapping注解
                        String methodName = method.getName();
                        //建立方法名和URL的映射
                        String urlMapping = method.getAnnotation(RequestMapping.class).value();
                        this.urlMappingNames.add(urlMapping);
                        this.mappingObjs.put(urlMapping, obj);
                        this.mappingMethods.put(urlMapping, method);
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sPath = request.getServletPath();
        System.out.println("sPath：" + sPath);
        if (!this.urlMappingNames.contains(sPath)) {
            return ;
        }
        Object obj = null;
        Object objResult = null;
        try {
            Method method = this.mappingMethods.get(sPath);
            obj = this.mappingObjs.get(sPath);
            objResult = method.invoke(obj);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // 将方法返回值写入response
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().append(objResult.toString());
    }
}
