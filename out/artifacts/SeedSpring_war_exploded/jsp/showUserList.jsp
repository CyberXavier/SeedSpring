<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>test12</title>
</head>
<body>

<span>
    id1 : <%= request.getAttribute("user1").toString() %>
</span>
<br/>
<span>
    id2 : <%= request.getAttribute("user2").toString() %>
</span>

</body>
</html>