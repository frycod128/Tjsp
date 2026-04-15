<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户登录</title>
</head>
<body>
<h2>用户登录</h2>
<!-- 使用EL表达式显示错误信息 -->
<span style="color: red;">${param.error}</span>

<form action="doLogin.jsp" method="post">
    用户名：<input type="text" name="username" /><br/><br/>
    密&nbsp;&nbsp;码：<input type="password" name="password" /><br/><br/>
    <input type="submit" value="登录" />
</form>
</body>
</html>