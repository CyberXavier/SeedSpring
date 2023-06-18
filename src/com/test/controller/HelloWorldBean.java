package com.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.RequestParam;
import com.minis.web.bind.annotation.ResponseBody;
import com.minis.web.servlet.ModelAndView;
import com.test.entity.Student;
import com.test.entity.User;
import com.test.service.BaseService;
import com.test.service.IAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloWorldBean {
	@Autowired
	BaseService baseservice;

	@Autowired
	IAction action;

	@RequestMapping("/testaop")
	public void doTestAop(HttpServletRequest request, HttpServletResponse response) {
		action.doAction();

		String str = "test aop, hello world!";
		try {
			response.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/testaop2")
	public void doTestAop2(HttpServletRequest request, HttpServletResponse response) {
		action.doSomething();

		String str = "test aop, hello world!";
		try {
			response.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/test2")
	public void doTest2(HttpServletRequest request, HttpServletResponse response) {
		String str = "test 2, hello world!";
		try {
			response.getWriter().write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping("/test5")
	public ModelAndView doTest5(User user) {
		ModelAndView mav = new ModelAndView("test","msg",user.getName());
		return mav;
	}
	@RequestMapping("/test6")
	public String doTest6(User user) {
		return "error";
	}

	@RequestMapping("/test7")
	@ResponseBody
	public User doTest7(User user) {
		System.out.println(user.getBirthday());
		user.setName(user.getName() + "---");
		//user.setBirthday(new Date());
		return user;
	}

	@RequestMapping("/test8")
	@ResponseBody
	public Student doTest8(Student student){
		System.out.println(student.getId());
		System.out.println(student.getName());
		System.out.println(student.getBirthday());
		student.setName(student.getName() + "你好！");
		return student;
	}

	@RequestMapping("/test9")
	@ResponseBody
	public Student doTest9(int id){
		Student student = new Student();
		student.setId(id);
		return student;
	}

	@RequestMapping("/test10")
	@ResponseBody
	public Student doTest10(double score){
		Student student = new Student();
		student.setScore(score);
		return student;
	}

	@RequestMapping("/test11")
	@ResponseBody
	public Student doTest11(float money){
		Student student = new Student();
		student.setMoney(money);
		return student;
	}

	@RequestMapping("/test12")
	public ModelAndView doTest11(double score, float money){
		Student student = new Student();
		student.setScore(score);
		student.setMoney(money);
		Map<String, Object> map = new HashMap<>();
		map.put("score", student.getScore());
		map.put("money", student.getMoney());
		ModelAndView mv = new ModelAndView("test12",map);
		return mv;
	}

	@RequestMapping("/test13")
	public ModelAndView doTest11(double score, Student student){
		student.setScore(score);
		Map<String, Object> map = new HashMap<>();
		map.put("score", student.getScore());
		map.put("money", student.getMoney());
		ModelAndView mv = new ModelAndView("test12",map);
		return mv;
	}

	@RequestMapping("/test14")
	public ModelAndView doTest14(@RequestParam(value = "s1") double score, float money){
		Student student = new Student();
		student.setScore(score);
		student.setMoney(money);
		Map<String, Object> map = new HashMap<>();
		map.put("score", student.getScore());
		map.put("money", student.getMoney());
		ModelAndView mv = new ModelAndView("test12",map);
		return mv;
	}

	@RequestMapping("/test15")
	@ResponseBody
	public Student doTest15(@RequestParam("ID") Integer id,
							@RequestParam(value = "myName") String name,
							double testScore,
							Student student){

		System.out.println("money : " + student.getMoney());
		System.out.println("birthday : " + student.getBirthday());
		System.out.println("score : " + student.getScore());
		System.out.println("id : " + student.getId());
		System.out.println("name : " + student.getName());
		student.setId(id);
		student.setName(name);
		student.setScore(testScore);
		return student;
	}

}
