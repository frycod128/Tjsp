<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${entityName} 管理</title>
</head>
<body>
<h2>${entityName} 管理</h2>

<a href="${pageContext.request.contextPath}/${entityName}?action=add">添加新记录</a>

<hr>

<form action="${pageContext.request.contextPath}/${entityName}" method="post">
    <input type="hidden" name="action" value="search">
    <select name="searchColumn">
        <c:forEach items="${columns}" var="col">
            <c:if test="${!col.isPrimaryKey}">
                <option value="${col.columnName}" ${col.columnName eq searchColumn ? 'selected' : ''}>
                        ${col.displayName}
                </option>
            </c:if>
        </c:forEach>
    </select>
    <input type="text" name="keyword" placeholder="输入关键词搜索..." value="${keyword}">
    <input type="submit" value="搜索">
    <a href="${pageContext.request.contextPath}/${entityName}?action=list">显示全部</a>
</form>

<hr>

<table border="1" cellspacing="0" cellpadding="5">
    <tr>
        <c:forEach items="${columns}" var="col">
            <th>${col.displayName}</th>
        </c:forEach>
        <th>操作</th>
    </tr>
    <c:choose>
        <c:when test="${not empty pageInfo.list}">
            <c:forEach items="${pageInfo.list}" var="entity">
                <tr>
                    <c:forEach items="${columns}" var="col">
                        <td>
                            <c:choose>
                                <c:when test="${col.columnName == 'id'}">
                                    ${entity.primaryKeyValue}
                                </c:when>
                                <c:when test="${col.columnName == 'model'}">
                                    ${entity.model}
                                </c:when>
                                <c:when test="${col.columnName == 'brand'}">
                                    ${entity.brand}
                                </c:when>
                                <c:when test="${col.columnName == 'driver_size'}">
                                    ${entity.driverSize}
                                </c:when>
                                <c:when test="${col.columnName == 'impedance'}">
                                    ${entity.impedance}
                                </c:when>
                                <c:when test="${col.columnName == 'sensitivity'}">
                                    ${entity.sensitivity}
                                </c:when>
                                <c:when test="${col.columnName == 'frequency_response'}">
                                    ${entity.frequencyResponse}
                                </c:when>
                                <c:when test="${col.columnName == 'price'}">
                                    ${entity.price}
                                </c:when>
                                <c:when test="${col.columnName == 'stock'}">
                                    ${entity.stock}
                                </c:when>
                                <c:when test="${col.columnName == 'wireless'}">
                                    ${entity.wireless}
                                </c:when>
                                <c:when test="${col.columnName == 'noise_cancelling'}">
                                    ${entity.noiseCancelling}
                                </c:when>
                                <c:when test="${col.columnName == 'create_time'}">
                                    ${entity.createTime}
                                </c:when>
                                <c:otherwise>
                                    ${col.columnName}
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:forEach>
                    <td>
                        <a href="${pageContext.request.contextPath}/${entityName}?action=edit&id=${entity.primaryKeyValue}">编辑</a>
                        <a href="${pageContext.request.contextPath}/${entityName}?action=delete&id=${entity.primaryKeyValue}"
                           onclick="return confirm('确定删除吗？')">删除</a>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="20" align="center">暂无数据</td>
            </tr>
        </c:otherwise>
    </c:choose>
</table>

<hr>

<div>
    <c:if test="${pageInfo.currentPage > 1}">
        <a href="${pageContext.request.contextPath}/${entityName}?action=list&currentPage=${pageInfo.currentPage - 1}">上一页</a>
    </c:if>
    <c:forEach begin="1" end="${pageInfo.totalPage}" var="pageNum">
        <c:choose>
            <c:when test="${pageNum == pageInfo.currentPage}">
                [${pageNum}]
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/${entityName}?action=list&currentPage=${pageNum}">${pageNum}</a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    <c:if test="${pageInfo.currentPage < pageInfo.totalPage}">
        <a href="${pageContext.request.contextPath}/${entityName}?action=list&currentPage=${pageInfo.currentPage + 1}">下一页</a>
    </c:if>
    &nbsp;&nbsp;
    共 ${pageInfo.totalCount} 条记录，第 ${pageInfo.currentPage}/${pageInfo.totalPage} 页
</div>
</body>
</html>