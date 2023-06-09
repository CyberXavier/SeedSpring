package com.minis.test;

import com.minis.beans.factory.annotation.Autowired;

public class BaseService {
	private String name;
	@Autowired
	private BaseBaseService bbs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BaseBaseService getBbs() {
		return bbs;
	}
	public void setBbs(BaseBaseService bbs) {
		this.bbs = bbs;
	}
	public BaseService() {
	}
	public void sayHello() {
		System.out.println("Base Service says hello");
		bbs.sayHello();
	}
}
