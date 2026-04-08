<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.HashMap" %>
<!DOCTYPE html>
<html>
<head>
    <title>杨辉三角与友情链接</title>
    <meta charset="UTF-8">
</head>
<body>
<h2>请输入杨辉三角的行数</h2>
<form action="yanghui.jsp" method="post">
    <label>行数 n (1-20):</label>
    <input type="text" name="n" pattern="[0-9]+" required>
    <input type="submit" value="生成杨辉三角">
</form>

<%
    // 使用Java 1.6兼容的语法，不使用钻石操作符
    HashMap siteMap = new HashMap();
    siteMap.put("百度", "https://www.baidu.com");
    siteMap.put("谷歌", "https://www.google.com");
    siteMap.put("GitHub", "https://github.com");
    siteMap.put("Stack Overflow", "https://stackoverflow.com");
    siteMap.put("必应", "https://www.bing.com");

    // 将HashMap存储到session中
    session.setAttribute("siteLinks", siteMap);
%>
</body>
</html>