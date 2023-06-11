package com.minis.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class WebUtils {
    public static Map<String, Object> getParametersStartingWith(HttpServletRequest request, String prefix){
        Enumeration<String> parameterNames = request.getParameterNames();
        // 创建一个TreeMap对象来存储参数名和对于值，使用TreeMap可以按照参数名的字典顺序排序
        Map<String, Object> params = new TreeMap<>();
        if (prefix == null) {
            prefix = "";
        }
        while (parameterNames != null && parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            // 如果前缀为空或者当前参数名以指定前缀开头
            if (prefix.isEmpty() || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String value = request.getParameter(paramName);

                params.put(unprefixed, value);
            }
        }
        return params;
    }
}
