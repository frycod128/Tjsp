<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录页面</title>
</head>
<body>
<h2>用户登录</h2>
<%
    String errorMsg = (String) request.getAttribute("errorMsg");
    if (errorMsg != null) {
        out.println("<p style='color:red'>" + errorMsg + "</p>");
    }
%>
<form action="LoginServlet" method="post">
    <label>用户名：</label>
    <input type="text" name="username" required><br><br>

    <label>密码：</label>
    <input type="password" name="password" required><br><br>

    <label>偶数n：</label>
    <input type="number" name="number" step="2" required><br><br>

    <input type="submit" value="登录">
</form>
</body>
</html>