<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    // 简单验证：用户名密码都为admin
    if ("admin".equals(username) && "admin".equals(password)) {
        session.setAttribute("username", username);
        response.sendRedirect("success.jsp");
    } else {
        response.sendRedirect("login.jsp?error=用户名或密码错误");
    }
%>