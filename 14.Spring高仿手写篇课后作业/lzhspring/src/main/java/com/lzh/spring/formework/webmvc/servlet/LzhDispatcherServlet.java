package com.lzh.spring.formework.webmvc.servlet;


import com.lzh.spring.formework.annotation.LzhController;
import com.lzh.spring.formework.annotation.LzhRequestMapping;
import com.lzh.spring.formework.context.LzhApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Servlet 只是作为一个 MVC 的启动入口
@Slf4j
public class LzhDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private LzhApplicationContext context;

    private List<LzhHandlerMapping> handlerMappings = new ArrayList<LzhHandlerMapping>();

    private Map<LzhHandlerMapping,LzhHandlerAdapter> handlerAdapters = new HashMap<LzhHandlerMapping,LzhHandlerAdapter>();

    private List<LzhViewResolver> viewResolvers = new ArrayList<LzhViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            this.doDispatch(req,resp);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        LzhHandlerMapping handler = getHandler(req);

        if(handler == null){
            //new ModelAndView("404")
            return;
        }

        //2、准备调用前的参数
        LzhHandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        LzhModelAndView mv = ha.handle(req,resp,handler);


        processDispatchResult(req, resp, mv);


    }

    private LzhHandlerMapping getHandler(HttpServletRequest req) throws Exception {
        if(this.handlerMappings.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (LzhHandlerMapping handler : this.handlerMappings) {
            try{
                Matcher matcher = handler.getPattern().matcher(url);
                //如果没有匹配上继续下一个匹配
                if(!matcher.matches()){ continue; }

                return handler;
            }catch(Exception e){
                throw e;
            }
        }
        return null;

    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, LzhModelAndView mv) {
    }

    private LzhHandlerAdapter getHandlerAdapter(LzhHandlerMapping handler) {
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        context = new LzhApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC 九大组件
        initStrategies(context);
    }



    //初始化策略
    protected void initStrategies(LzhApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);


        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);


        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(LzhApplicationContext context) {
    }

    private void initViewResolvers(LzhApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(LzhApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(LzhApplicationContext context) {
    }

    private void initHandlerAdapters(LzhApplicationContext context) {

        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参

        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (LzhHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new LzhHandlerAdapter());
        }
    }

    private void initHandlerMappings(LzhApplicationContext context) {

        String [] beanNames = context.getBeanDefinitionNames();

        try {

            for (String beanName : beanNames) {

                Object controller = context.getBean(beanName);

                Class<?> clazz = controller.getClass();

                if(!clazz.isAnnotationPresent(LzhController.class)){
                    continue;
                }

                String baseUrl = "";
                //获取Controller的url配置
                if(clazz.isAnnotationPresent(LzhRequestMapping.class)){
                    LzhRequestMapping requestMapping = clazz.getAnnotation(LzhRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //获取Method的url配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {

                    //没有加RequestMapping注解的直接忽略
                    if(!method.isAnnotationPresent(LzhRequestMapping.class)){ continue; }

                    //映射URL
                    LzhRequestMapping requestMapping = method.getAnnotation(LzhRequestMapping.class);
                    //  /demo/query

                    //  (//demo//query)

                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);

                    this.handlerMappings.add(new LzhHandlerMapping(pattern,controller,method));
                    log.info("Mapped " + regex + "," + method);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void initThemeResolver(LzhApplicationContext context) {


        //拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new LzhViewResolver(templateRoot));
        }

    }

    private void initLocaleResolver(LzhApplicationContext context) {
    }

    private void initMultipartResolver(LzhApplicationContext context) {
    }


}
