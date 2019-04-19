package com.lzh.spring.formework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Tom on 2019/4/13.
 */
public class LzhHandlerAdapter {
    public boolean supports(Object handler) {
        return (handler instanceof LzhHandlerMapping);
    }


    LzhModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return null;
    }
}
