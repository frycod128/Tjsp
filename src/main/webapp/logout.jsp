<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 清空Session
    session.invalidate();
    // 重定向到登录页
    response.sendRedirect("login.jsp");
%>