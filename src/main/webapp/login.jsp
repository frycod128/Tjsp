<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录页面</title>
    <script>
        function validateForm() {
            var username = document.getElementById("username").value.trim();
            var password = document.getElementById("password").value;
            var numberInput = document.getElementById("number");
            var n = parseInt(numberInput.value);

            // 用户名校验
            if (username === "") {
                alert("用户名不能为空！");
                return false;
            }

            // 密码校验
            if (password === "") {
                alert("密码不能为空！");
                return false;
            }

            // 数字校验
            if (numberInput.value.trim() === "") {
                alert("请输入偶数n！");
                return false;
            }

            if (isNaN(n)) {
                alert("请输入有效的数字！");
                return false;
            }

            if (n % 2 !== 0) {
                alert("请输入偶数！");
                return false;
            }

            if (n <= 100) {
                alert("请输入大于100的偶数！");
                return false;
            }

            return true;
        }
    </script>
</head>
<body>
<div class="form-container">
    <h2>用户登录</h2>

    <%
        String errorMsg = (String) request.getAttribute("errorMsg");
        if (errorMsg != null) {
            out.println("<div class='error-message'>❌ " + errorMsg + "</div>");
        }
    %>

    <form action="LoginServlet" method="post" onsubmit="return validateForm()">
        <div class="form-group">
            <label>用户名：</label>
            <input type="text" id="username" name="username" required>
        </div>

        <div class="form-group">
            <label>密码：</label>
            <input type="password" id="password" name="password" required>
        </div>

        <div class="form-group">
            <label>偶数n：</label>
            <input type="number" id="number" name="number" step="2" required>
            <div class="hint">请输入大于100的偶数（如：102、104等）</div>
        </div>

        <div class="form-group">
            <input type="submit" value="登录验证">
        </div>
    </form>
</div>
</body>
</html>