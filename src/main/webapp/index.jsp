<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="cn.yznu.abc321.util.DbConfigLoader" %>
<%@ page import="cn.yznu.abc321.entity.Condition" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:if test="${empty tables}">
    <c:set var="tables" value="<%= DbConfigLoader.getQueryableTables() %>" scope="request"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>数据库查询</title>
    <style>
        body{font-family:sans-serif;margin:40px;}
        .msg{color:#c00;margin:12px 0;}
        form{margin:12px 0;}
        .result-box{border:1px solid #ccc;padding:16px;margin-top:16px;background:#fafafa;max-width:1100px;}
        fieldset{margin:16px 0;padding:12px;border:1px solid #aaa;}
        legend{font-weight:bold;}
        .multi-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(280px,1fr));gap:6px;}
        .multi-item{display:flex;align-items:center;gap:6px;font-size:14px;}
        .multi-item label{min-width:130px;text-align:right;}
        .multi-item input[type=text]{flex:1;padding:2px 4px;}
        .summary{background:#eee;padding:8px;margin:8px 0;font-size:14px;}
    </style>
</head>
<body>

<h2>数据库查询 — <%= DbConfigLoader.getDbName() %></h2>

<c:if test="${not empty msg}"><div class="msg">${msg}</div></c:if>

<!-- ==================== 步骤1：选表 ==================== -->
<form method="post" action="query">
    <select name="table">
        <option value="">-- 选择表 --</option>
        <c:forEach items="${tables}" var="t">
            <option value="${t}" ${t eq table ? 'selected' : ''}>${t}</option>
        </c:forEach>
    </select>
    <input type="hidden" name="action" value="single"/>
    <button type="submit">选表（单键查询）</button>
    &nbsp;
    <button type="button" onclick="goMulti()">多字段联合查询</button>
    &nbsp;
    <button type="button" onclick="goBatch()">批量模糊查询</button>
</form>

<script>
    function goMulti(){var t=document.querySelector('[name=table]').value;if(!t){alert('请先选表');return;}var f=document.createElement('form');f.method='post';f.action='query';f.innerHTML='<input name=table value='+t+'><input name=action value=multi>';document.body.appendChild(f);f.submit();}
    function goBatch(){var t=document.querySelector('[name=table]').value;if(!t){alert('请先选表');return;}var f=document.createElement('form');f.method='post';f.action='query';f.innerHTML='<input name=table value='+t+'><input name=action value=batch>';document.body.appendChild(f);f.submit();}
</script>

<!-- ==================== 单键查询：选键+值 ==================== -->
<c:if test="${not empty table and empty expandedRows and empty conditions and empty fuzzyColumn and not showMulti and not showBatch}">
    <form method="post" action="query">
        <input type="hidden" name="table" value="${table}"/>
        <input type="hidden" name="action" value="single"/>
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

<!-- ==================== 多字段 AND 联合查询 ==================== -->
<c:if test="${not empty table and (showMulti or (empty expandedRows and not empty conditions eq false and empty fuzzyColumn and empty searchKey))}">
    <c:if test="${showMulti or param.action eq 'multi'}">
        <fieldset>
            <legend>多字段 AND 联合查询 — ${table}</legend>
            <form method="post" action="query">
                <input type="hidden" name="table" value="${table}"/>
                <input type="hidden" name="action" value="multi"/>
                <div class="multi-grid">
                    <c:forEach items="${columns}" var="col">
                        <div class="multi-item">
                            <label>${col.value}</label>
                            <input type="text" name="c_${col.key}" placeholder="${col.key}"/>
                            <label style="min-width:auto;font-size:12px;">
                                <input type="checkbox" name="f_${col.key}"/>模糊
                            </label>
                        </div>
                    </c:forEach>
                </div>
                <p><button type="submit">AND 联合查询</button></p>
            </form>
        </fieldset>
    </c:if>
</c:if>

<!-- 多字段查询未选时也显示（回显用） -->
<c:if test="${not empty table and showMulti and empty expandedRows}">
    <c:set var="__showMultiForm" value="true"/>
</c:if>

<!-- ==================== 批量模糊查询 ==================== -->
<c:if test="${not empty table and (showBatch or (empty expandedRows and empty conditions and empty fuzzyColumn and empty searchKey))}">
    <c:if test="${showBatch or param.action eq 'batch'}">
        <%
            String t = (String) request.getAttribute("table");
            java.util.List<String> fcs = DbConfigLoader.getFuzzyColumns(t);
            request.setAttribute("_fuzzyCols", fcs);
        %>
        <fieldset>
            <legend>批量模糊查询 — ${table}</legend>
            <form method="post" action="query">
                <input type="hidden" name="table" value="${table}"/>
                <input type="hidden" name="action" value="batch"/>
                <p>
                    模糊列：
                    <select name="fuzzyColumn">
                        <c:forEach items="${_fuzzyCols}" var="fc">
                            <option value="${fc}">${columns[fc]} (${fc})</option>
                        </c:forEach>
                    </select>
                </p>
                <p>
                    查询值（多个用逗号分隔）：
                    <input type="text" name="fuzzyValues" placeholder="如：XM5, M50x" size="40"/>
                </p>
                <button type="submit">OR 模糊查询</button>
            </form>
        </fieldset>
    </c:if>
</c:if>

<!-- ==================== 单键查询结果 ==================== -->
<c:if test="${not empty expandedRows and not empty searchKey}">
    <div class="summary">
        表 <b>${table}</b>，条件：<b>${columns[searchKey]}(${searchKey}) = ${searchValue}</b>，
        共 <b>${expandedRows.size()}</b> 行
    </div>
    <div class="result-box">
        <c:forEach items="${expandedRows}" var="row">
            <t:renderRow row="${row}" depth="0"/>
        </c:forEach>
    </div>
    <p><a href="query">← 重新查询</a></p>
</c:if>

<!-- ==================== 多字段查询结果 ==================== -->
<c:if test="${not empty expandedRows and not empty conditions}">
    <div class="summary">
        表 <b>${table}</b>，条件：
        <c:forEach items="${conditions}" var="cnd" varStatus="st">
            <c:if test="${!st.first}"> <b>AND</b> </c:if>
            ${columns[cnd.column]}(${cnd.column}) <c:if test="${cnd.fuzzy}">≈</c:if><c:if test="${!cnd.fuzzy}">=</c:if> ${cnd.value}
        </c:forEach>
        ，共 <b>${expandedRows.size()}</b> 行
    </div>
    <div class="result-box">
        <c:forEach items="${expandedRows}" var="row">
            <t:renderRow row="${row}" depth="0"/>
        </c:forEach>
    </div>
    <p>
        <a href="query">← 重新查询</a>
        &nbsp;|&nbsp;
    <form method="post" action="query" style="display:inline;">
        <input type="hidden" name="table" value="${table}"/>
        <input type="hidden" name="action" value="multi"/>
        <button type="submit">← 同表继续</button>
    </form>
    </p>
</c:if>

<!-- ==================== 批量模糊查询结果 ==================== -->
<c:if test="${not empty expandedRows and not empty fuzzyColumn}">
    <div class="summary">
        表 <b>${table}</b>，
        列 <b>${columns[fuzzyColumn]}(${fuzzyColumn})</b> ≈ <b>[${fuzzyValues}]</b>，
        共 <b>${expandedRows.size()}</b> 行
    </div>
    <div class="result-box">
        <c:forEach items="${expandedRows}" var="row">
            <t:renderRow row="${row}" depth="0"/>
        </c:forEach>
    </div>
    <p>
        <a href="query">← 重新查询</a>
        &nbsp;|&nbsp;
    <form method="post" action="query" style="display:inline;">
        <input type="hidden" name="table" value="${table}"/>
        <input type="hidden" name="action" value="batch"/>
        <button type="submit">← 同表继续</button>
    </form>
    </p>
</c:if>

</body>
</html>
