package com.lzh.demo.service;


import com.lzh.mvcframework.annotation.LzhService;

;
/**
 * 核心业务逻辑
 */
@LzhService
public class DemoService implements IDemoService {

    @Override
    public String get(String name) {
        return "My name is " + name;
    }
}
