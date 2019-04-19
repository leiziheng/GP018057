package com.lzh.spring.formework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * Created by Tom on 2019/4/13.
 */
public class LzhViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    private File templateRootDir;
    public LzhViewResolver(String templateRoot) {

    }

    public LzhView resolveViewName(String viewName, Locale locale) throws Exception {
        if (null == viewName || "" .equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new LzhView(templateFile);
    }
}
