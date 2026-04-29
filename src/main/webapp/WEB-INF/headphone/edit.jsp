<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑 ${entityName}</title>
</head>
<body>
<h2>编辑 ${entityName}</h2>

<form action="${pageContext.request.contextPath}/${entityName}" method="post">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="${entity.primaryKeyValue}">
    <table border="0" cellspacing="5" cellpadding="5">
        <c:forEach items="${columns}" var="col">
            <c:if test="${!col.isPrimaryKey && col.columnName != 'create_time'}">
                <tr>
                    <td align="right">${col.displayName}:</td>
                    <td>
                        <c:choose>
                            <c:when test="${col.columnName == 'wireless'}">
                                <select name="wireless">
                                    <option value="1" ${entity.wireless ? 'selected' : ''}>是</option>
                                    <option value="0" ${!entity.wireless ? 'selected' : ''}>否</option>
                                </select>
                            </c:when>
                            <c:when test="${col.columnName == 'noise_cancelling'}">
                                <select name="noise_cancelling">
                                    <option value="1" ${entity.noiseCancelling ? 'selected' : ''}>是</option>
                                    <option value="0" ${!entity.noiseCancelling ? 'selected' : ''}>否</option>
                                </select>
                            </c:when>
                            <c:when test="${col.columnName == 'model'}">
                                <input type="text" name="model" value="${entity.model}" required>
                            </c:when>
                            <c:when test="${col.columnName == 'brand'}">
                                <input type="text" name="brand" value="${entity.brand}" required>
                            </c:when>
                            <c:when test="${col.columnName == 'driver_size'}">
                                <input type="text" name="driver_size" value="${entity.driverSize}" required>
                            </c:when>
                            <c:when test="${col.columnName == 'impedance'}">
                                <input type="text" name="impedance" value="${entity.impedance}" required>
                            </c:when>
                            <c:when test="${col.columnName == 'sensitivity'}">
                                <input type="text" name="sensitivity" value="${entity.sensitivity}" required>
                            </c:when>
                            <c:when test="${col.columnName == 'frequency_response'}">
                                <input type="text" name="frequency_response" value="${entity.frequencyResponse}" required>
                            </c:when>
                            <c:when test="${col.columnName == 'price'}">
                                <input type="text" name="price" value="${entity.price}" required>
                            </c:when>
                            <c:when test="${col.columnName == 'stock'}">
                                <input type="text" name="stock" value="${entity.stock}" required>
                            </c:when>
                            <c:otherwise>
                                <input type="text" name="${col.columnName}" value="${entity[col.columnName]}">
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        <tr>
            <td colspan="2" align="center">
                <input type="submit" value="更新">
                <a href="${pageContext.request.contextPath}/${entityName}?action=list">
                    <input type="button" value="返回">
                </a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>