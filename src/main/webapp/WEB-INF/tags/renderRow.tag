<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="row" type="cn.yznu.abc321.entity.ExpandableRow" required="true" %>
<%@ attribute name="depth" type="java.lang.Integer" required="true" %>

<c:set var="indent" value="${depth * 24}" />
<c:set var="cols" value="${row.columnLabels}" />

<div style="margin-left:${indent}px; padding:3px 0; font-size:${depth==0?'15px':depth==1?'13px':'12px'}">
    <b>[${row.tableName}]</b>
    <c:forEach items="${cols}" var="col" varStatus="st">
        ${col.value}=<c:choose><c:when test="${row.rowData[col.key] != null}">${row.rowData[col.key]}</c:when><c:otherwise>-</c:otherwise></c:choose><c:if test="${!st.last}">, </c:if>
    </c:forEach>
</div>

<c:if test="${not empty row.children}">
    <c:forEach items="${row.children}" var="entry">
        <div style="margin-left:${indent + 20}px; font-style:italic; font-size:12px; color:#555; padding:2px 0;">
            ▸ ${entry.key}（${entry.value.size()} 行）
        </div>
        <c:forEach items="${entry.value}" var="child">
            <c:set var="__row" value="${child}" scope="request"/>
            <c:set var="__d" value="${depth + 1}" scope="request"/>
            <jsp:include page="/WEB-INF/tags/renderOne.jsp"/>
        </c:forEach>
    </c:forEach>
</c:if>
