<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>头戴式耳机管理</title>
</head>
<body>
<h2>头戴式耳机管理</h2>

<a href="${pageContext.request.contextPath}/headphone/add" class="btn-add">添加新耳机</a>

<div class="search-box">
    <form action="${pageContext.request.contextPath}/headphone/search" method="post">
        <input type="text" name="keyword" placeholder="输入型号搜索..." value="${keyword}">
        <button type="submit" class="btn-edit">搜索</button>
        <a href="${pageContext.request.contextPath}/headphone/list">
            <button type="button" class="btn-edit">显示全部</button>
        </a>
    </form>
</div>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>品牌</th>
        <th>型号</th>
        <th>驱动单元(mm)</th>
        <th>阻抗(Ω)</th>
        <th>灵敏度(dB)</th>
        <th>频响范围</th>
        <th>价格(¥)</th>
        <th>库存</th>
        <th>特性</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${not empty pageInfo.list}">
            <c:forEach items="${pageInfo.list}" var="hp">
                <tr>
                    <td>${hp.id}</td>
                    <td>${hp.brand}</td>
                    <td>${hp.model}</td>
                    <td>${hp.driverSize}</td>
                    <td>${hp.impedance}</td>
                    <td>${hp.sensitivity}</td>
                    <td>${hp.frequencyResponse}</td>
                    <td>${hp.price}</td>
                    <td>${hp.stock}</td>
                    <td>
                        <c:if test="${hp.wireless}">无线 </c:if>
                        <c:if test="${hp.noiseCancelling}">降噪</c:if>
                        <c:if test="${!hp.wireless && !hp.noiseCancelling}">有线</c:if>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/headphone/edit?id=${hp.id}">
                            <button class="btn-edit">编辑</button>
                        </a>
                        <a href="${pageContext.request.contextPath}/headphone/delete?id=${hp.id}"
                           onclick="return confirm('确定删除吗？')">
                            <button class="btn-delete">删除</button>
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="11" style="text-align: center;">暂无数据</td>
            </tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<div class="pagination">
    <c:if test="${pageInfo.currentPage > 1}">
        <a href="${pageContext.request.contextPath}/headphone/list?currentPage=${pageInfo.currentPage - 1}">上一页</a>
    </c:if>

    <c:forEach begin="1" end="${pageInfo.totalPage}" var="pageNum">
        <a href="${pageContext.request.contextPath}/headphone/list?currentPage=${pageNum}"
           class="${pageNum == pageInfo.currentPage ? 'active' : ''}">${pageNum}</a>
    </c:forEach>

    <c:if test="${pageInfo.currentPage < pageInfo.totalPage}">
        <a href="${pageContext.request.contextPath}/headphone/list?currentPage=${pageInfo.currentPage + 1}">下一页</a>
    </c:if>

    <span style="margin-left: 20px;">
            共 ${pageInfo.totalCount} 条记录，第 ${pageInfo.currentPage}/${pageInfo.totalPage} 页
        </span>
</div>
</body>
</html>