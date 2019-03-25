package com.lzh.mvcframework.v2.servlet;

import com.lzh.mvcframework.annotation.LzhAutowired;
import com.lzh.mvcframework.annotation.LzhController;
import com.lzh.mvcframework.annotation.LzhRequestMapping;
import com.lzh.mvcframework.annotation.LzhService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class lzhdispatcherServlet extends HttpServlet {

    //存储 aplication.properties 的配置内容
    private Properties contextConfig = new Properties();
    //存储所有扫描到的类
    private List<String> classNames = new ArrayList<String>();
    //IOC 容器， 保存所有实例化对象
    private Map<String, Object> ioc = new HashMap<String, Object>();
    //保存 Contrller 中所有 Mapping 的对应关系
    private Map<String, Method> handlerMapping = new HashMap<String, Method>();

    public lzhdispatcherServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //派遣， 分发任务
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Excetion Detail:" + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found!!");
            return;
        }
        Method method = this.handlerMapping.get(url);
        //第一个参数： 方法所在的实例
        //第二个参数： 调用时所需要的实参
        Map<String, String[]> params = req.getParameterMap();
        //投机取巧的方式
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(ioc.get(beanName), new Object[]{req, resp, params.get("name")[0]});
        //System.out.println(method);
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、 加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2、 扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
        //3、 初始化所有相关的类的实例， 并且放入到 IOC 容器之中
        doInstance();
        //4、 完成依赖注入
        doAutowired();
        //5、 初始化 HandlerMapping
        initHandlerMapping();

        System.out.println("GP Spring framework is init.");
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(LzhController.class)) {
                continue;
            }
            String baseUrl = "";
            //获取 Controller 的 url 配置
            if (clazz.isAnnotationPresent(LzhRequestMapping.class)) {
                LzhRequestMapping requestMapping = clazz.getAnnotation(LzhRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //获取 Method 的 url 配置
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                //没有加 RequestMapping 注解的直接忽略
                if (!method.isAnnotationPresent(LzhRequestMapping.class)) {
                    continue;
                }
                //映射 URL
                LzhRequestMapping requestMapping = method.getAnnotation(LzhRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("/+", "/");
                handlerMapping.put(url, method);
                System.out.println("Mapped " + url + "," + method);
            }
        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            //拿到实例对象中的所有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(LzhAutowired.class)) {
                    continue;
                }
                LzhAutowired autowired = field.getAnnotation(LzhAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                //不管你愿不愿意， 强吻
                field.setAccessible(true); //设置私有属性的访问权限
                try {
                    //执行注入动作
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {

                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(LzhController.class)) {
                    Object instance = clazz.newInstance();
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(LzhService.class)) {
                    //1、 默认的类名首字母小写
                    String beanName = toLowerFirstCase(clazz.getSimpleName());

                    //2、 自定义命名
                    LzhService service = clazz.getAnnotation(LzhService.class);
                    if (!"".equals(service.value())) {
                        beanName = service.value();
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);

                    //3、 根据类型注入实现类， 投机取巧的方式
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("The beanName is exists!!");
                        }
                        ioc.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String scanPackage) {

        //包传过来包下面的所有的类全部扫描进来的
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName()).replace(".class", "");
                classNames.add(className);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {

        InputStream fis = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            //1、 读取配置文件
            contextConfig.load(fis);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //首字母转成小写
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
