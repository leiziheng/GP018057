package com.lzh.demo.controller;

import com.lzh.demo.service.IDemoService;
import com.lzh.mvcframework.annotation.LzhAutowired;
import com.lzh.mvcframework.annotation.LzhController;
import com.lzh.mvcframework.annotation.LzhRequestMapping;
import com.lzh.mvcframework.annotation.LzhRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@LzhController
@LzhRequestMapping("/demo")
public class DemoController {

    @LzhAutowired
    private IDemoService demoService;

    @LzhRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @LzhRequestParam("name") String name){
        String result = demoService.get(name);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
