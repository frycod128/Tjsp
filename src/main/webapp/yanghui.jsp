<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>杨辉三角结果</title>
    <meta charset="UTF-8">
    <style>
        table {
            border-collapse: collapse;
            margin: 20px 0;
        }
        td {
            border: 1px solid #ccc;
            padding: 8px 15px;
            text-align: center;
        }
        .link-list {
            margin-top: 30px;
            padding: 15px;
            background-color: #f5f5f5;
            border-radius: 5px;
        }
        .link-list a {
            margin-right: 20px;
            text-decoration: none;
            color: #0066cc;
        }
        .link-list a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<h2>杨辉三角形</h2>

<%
    // 从请求参数获取n（因为用的是post方法）
    String nStr = request.getParameter("n");
    Integer n = null;

    if (nStr != null && !nStr.trim().isEmpty()) {
        try {
            n = Integer.parseInt(nStr.trim());
        } catch (NumberFormatException e) {
            n = null;
        }
    }

    if (n == null || n <= 0 || n > 20) {
%>
<p style="color: red;">请输入1到20之间的有效整数！</p>
<a href="index.jsp">返回重新输入</a>
<%
} else {
    // 构建杨辉三角二维数组
    int[][] triangle = new int[n][];
    for (int i = 0; i < n; i++) {
        triangle[i] = new int[i + 1];
        triangle[i][0] = 1;
        triangle[i][i] = 1;
        for (int j = 1; j < i; j++) {
            triangle[i][j] = triangle[i-1][j-1] + triangle[i-1][j];
        }
    }
%>
<table>
    <%
        for (int i = 0; i < n; i++) {
            out.println("<tr>");
            for (int j = 0; j <= i; j++) {
                out.println("<td>" + triangle[i][j] + "</td>");
            }
            out.println("</tr>");
        }
    %>
</table>
<%
    }
%>

<div class="link-list">
    <h3>友情链接</h3>
    <p>
        <%
            // 从session获取HashMap
            HashMap siteLinks = (HashMap) session.getAttribute("siteLinks");
            if (siteLinks != null) {
                Iterator iterator = siteLinks.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String siteName = (String) entry.getKey();
                    String siteUrl = (String) entry.getValue();
        %>
        <a href="<%= siteUrl %>" target="_blank"><%= siteName %></a>&nbsp;&nbsp;&nbsp;
        <%
                }
            }
        %>
    </p>
    <p style="font-size: 12px; color: #666; margin-top: 10px;">
        (点击链接将在新窗口打开)
    </p>
</div>

<p><a href="index.jsp">← 返回重新输入</a></p>
</body>
</html>