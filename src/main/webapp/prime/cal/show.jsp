<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>计算结果</title>
</head>
<body>
<%
    // 检查session，如果未登录则重定向到登录页面
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String username = (String) request.getAttribute("username");
    Integer n = (Integer) request.getAttribute("n");
    Integer prime1 = (Integer) request.getAttribute("prime1");
    Integer prime2 = (Integer) request.getAttribute("prime2");

    if (username == null || n == null || prime1 == null || prime2 == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<h2>哥德巴赫猜想验证结果</h2>
<p>用户名：<%= username %></p>
<p>输入的偶数 n：<%= n %></p>
<p>分解结果：<%= n %> = <%= prime1 %> + <%= prime2 %></p>
<p><%= prime1 %> 和 <%= prime2 %> 都是质数</p>

<br>
<a href="LoginServlet?action=logout">退出登录</a>
</body>
</html>