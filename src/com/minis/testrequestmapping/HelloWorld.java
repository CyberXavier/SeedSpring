package com.minis.testrequestmapping;

import com.minis.web.RequestMapping;

public class HelloWorld {
    @RequestMapping("/test")
    public static String doTest() {
        return "Hello World for doGet!!! 老子成功了！！！";
    }
}