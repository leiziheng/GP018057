package com.lzh.spring.formework;


import com.lzh.spring.demo.action.MyAction;
import com.lzh.spring.formework.context.LzhApplicationContext;

/**
 * Created by Tom on 2019/4/13.
 */
public class Test {

    public static void main(String[] args) {

        LzhApplicationContext context = new LzhApplicationContext("classpath:application.properties");
        try {
            Object object = context.getBean(MyAction.class);
            System.out.println(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
