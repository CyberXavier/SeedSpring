package com.minis.test;

public class BaseBaseService {
	private String name;
	private AServiceImpl as;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
}
