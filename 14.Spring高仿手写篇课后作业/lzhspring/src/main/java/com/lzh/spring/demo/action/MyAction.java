package com.lzh.spring.demo.action;


import com.lzh.spring.demo.service.IModifyService;
import com.lzh.spring.demo.service.IQueryService;
import com.lzh.spring.formework.annotation.LzhAutowired;
import com.lzh.spring.formework.annotation.LzhController;
import com.lzh.spring.formework.annotation.LzhRequestMapping;
import com.lzh.spring.formework.annotation.LzhRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口url
 * @author Tom
 *
 */
@LzhController
@LzhRequestMapping("/web")
public class MyAction {

	@LzhAutowired
	IQueryService queryService;
	@LzhAutowired
	IModifyService modifyService;

	@LzhRequestMapping("/query.json")
	public void query(HttpServletRequest request, HttpServletResponse response,
								@LzhRequestParam("name") String name){
		String result = queryService.query(name);
		out(response,result);
	}
	
	@LzhRequestMapping("/add*.json")
	public void add(HttpServletRequest request,HttpServletResponse response,
			   @LzhRequestParam("name") String name,@LzhRequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@LzhRequestMapping("/remove.json")
	public void remove(HttpServletRequest request,HttpServletResponse response,
		   @LzhRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@LzhRequestMapping("/edit.json")
	public void edit(HttpServletRequest request,HttpServletResponse response,
			@LzhRequestParam("id") Integer id,
			@LzhRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}
	
	
	
	private void out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
