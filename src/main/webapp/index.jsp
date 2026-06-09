<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>数据库查询</title>
    <style>
        body{font-family:sans-serif;margin:40px;}
        table{border-collapse:collapse;margin:16px 0;width:100%;max-width:960px;}
        th,td{border:1px solid #999;padding:6px 10px;text-align:center;}
        th{background:#eee;}
        .msg{color:#c00;margin:12px 0;}
        form{margin:12px 0;}
        label{display:inline-block;margin:3px 18px 3px 0;}
    </style>
</head>
<body>

<h2>数据库查询 — headphone_sj8</h2>

<c:if test="${not empty msg}"><div class="msg">${msg}</div></c:if>

<!-- 步骤1：选表 -->
<form method="post" action="query">
    <select name="table">
        <option value="">-- 选择表 --</option>
        <c:forEach items="${tables}" var="t">
            <option value="${t}" ${t eq table ? 'selected' : ''}>${t}</option>
        </c:forEach>
    </select>
    <button type="submit">选表</button>
</form>

<!-- 步骤2：选列 -->
<c:if test="${not empty table and empty results}">
    <form method="post" action="query">
        <input type="hidden" name="table" value="${table}"/>
        <c:forEach items="${columns}" var="col">
            <label>
                <input type="checkbox" name="cols" value="${col.key}" checked/>
                    ${col.value} <small>(${col.key})</small>
            </label><br/>
        </c:forEach>
        <button type="submit">查询</button>
    </form>
</c:if>

<!-- 步骤3：结果 -->
<c:if test="${not empty results}">
    <h3>${table}</h3>
    <table>
        <tr>
            <c:forEach items="${selectedCols}" var="col">
                <th>${columns[col]}</th>
            </c:forEach>
        </tr>
        <c:forEach items="${results}" var="row">
            <tr>
                <c:forEach items="${selectedCols}" var="col">
                    <td>${row[col]}</td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>
    <a href="query">← 重新查询</a>
</c:if>

</body>
</html>
