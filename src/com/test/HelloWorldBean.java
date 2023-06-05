package com.test;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.RequestMapping;
import com.test.service.BaseService;

public class HelloWorldBean {
	@Autowired
	BaseService baseservice; //注意这个类时com.test.service包下的不是com.minis.test包下的
	
	@RequestMapping("/test1")
	public String doTest1() {
		return "test 1, hello world!";
	}
	@RequestMapping("/test2")
	public String doTest2() {
		return "test 2, hello world!";
	}
	@RequestMapping("/test3")
	public String doTest3() {
		return baseservice.getHello();
	}}
