package com.test.service;

public class BaseBaseService {
	private String name;
	private AServiceImpl as;
	
	public AServiceImpl getAs() {
		return as;
	}
	public void setAs(AServiceImpl as) {
		this.as = as;
	}
	public BaseBaseService() {
	}
	public void sayHello() {
		System.out.println("Base Base Service says hello");

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
