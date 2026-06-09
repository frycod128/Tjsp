<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户购买记录查询</title>
    <style>
        body { font-family: sans-serif; margin: 40px; }
        table { border-collapse: collapse; margin-top: 20px; width: 100%; max-width: 800px; }
        th, td { border: 1px solid #999; padding: 8px 12px; text-align: center; }
        th { background: #eee; }
        .msg { color: #c00; margin-top: 16px; }
    </style>
</head>
<body>

<h2>根据手机号查询购买记录</h2>
<form action="query" method="get">
    <input type="text" name="phone" placeholder="输入手机号" value="${phone}" />
    <button type="submit">查询</button>
</form>

<c:if test="${not empty msg}">
    <div class="msg">${msg}</div>
</c:if>

<c:if test="${not empty records}">
    <table>
        <tr>
            <th>用户名</th>
            <th>手机号</th>
            <th>耳机型号</th>
            <th>品牌</th>
            <th>单价(元)</th>
            <th>数量</th>
            <th>下单时间</th>
        </tr>
        <c:forEach items="${records}" var="r">
            <tr>
                <td>${r.username}</td>
                <td>${r.phone}</td>
                <td>${r.model}</td>
                <td>${r.brand}</td>
                <td>${r.price}</td>
                <td>${r.quantity}</td>
                <td>${r.orderTime}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

</body>
</html>
