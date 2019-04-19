package com.lzh.spring.demo.action;


import com.lzh.spring.demo.service.IModifyService;
import com.lzh.spring.demo.service.IQueryService;
import com.lzh.spring.formework.annotation.LzhAutowired;
import com.lzh.spring.formework.annotation.LzhController;
import com.lzh.spring.formework.annotation.LzhRequestMapping;
import com.lzh.spring.formework.annotation.LzhRequestParam;
import com.lzh.spring.formework.webmvc.servlet.LzhModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
	public LzhModelAndView query(HttpServletRequest request, HttpServletResponse response,
								 @LzhRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@LzhRequestMapping("/add*.json")
	public LzhModelAndView add(HttpServletRequest request,HttpServletResponse response,
			   @LzhRequestParam("name") String name,@LzhRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
//			e.printStackTrace();
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getCause().getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new LzhModelAndView("500",model);
		}

	}
	
	@LzhRequestMapping("/remove.json")
	public LzhModelAndView remove(HttpServletRequest request,HttpServletResponse response,
		   @LzhRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@LzhRequestMapping("/edit.json")
	public LzhModelAndView edit(HttpServletRequest request,HttpServletResponse response,
			@LzhRequestParam("id") Integer id,
			@LzhRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private LzhModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
