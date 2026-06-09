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
        th,td{border:1px solid #999;padding:6px 10px;}
        th{background:#eee;}
        .msg{color:#c00;margin:12px 0;}
        form{margin:12px 0;}
    </style>
</head>
<body>

<h2>数据库查询 — <%= cn.yznu.abc321.util.DbConfigLoader.getDbName() %></h2>

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

<!-- 步骤2：选键 + 输入值 -->
<c:if test="${not empty table and empty results and empty searchKey}">
    <form method="post" action="query">
        <input type="hidden" name="table" value="${table}"/>
        <p>
            查询键：
            <select name="key">
                <c:forEach items="${columns}" var="col">
                    <option value="${col.key}">${col.value} (${col.key})</option>
                </c:forEach>
            </select>
        </p>
        <p>
            查询值：<input type="text" name="value" placeholder="输入值"/>
        </p>
        <button type="submit">查询</button>
    </form>
</c:if>

<!-- 步骤3：结果 -->
<c:if test="${not empty results}">
    <p>
        表 <b>${table}</b>，条件：<b>${columns[searchKey]}(${searchKey}) = ${searchValue}</b>
    </p>
    <table>
        <tr>
            <c:forEach items="${columns}" var="col">
                <th>${col.value}</th>
            </c:forEach>
        </tr>
        <c:forEach items="${results}" var="row">
            <tr>
                <c:forEach items="${columns}" var="col">
                    <td>${row[col.key]}</td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>
    <p>
        <a href="query">← 重新查询</a>
        &nbsp;|&nbsp;
        <a href="query?table=${table}">← 同表换条件</a>
    </p>
</c:if>

<c:if test="${not empty searchKey and not empty results}">
    <!-- 结果区已覆盖此分支 -->
</c:if>

</body>
</html>
