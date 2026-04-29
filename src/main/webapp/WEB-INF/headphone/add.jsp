<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>添加 ${entityName}</title>
</head>
<body>
<h2>添加 ${entityName}</h2>

<form action="${pageContext.request.contextPath}/${entityName}" method="post">
    <input type="hidden" name="action" value="save">
    <table border="0" cellspacing="5" cellpadding="5">
        <c:forEach items="${columns}" var="col">
            <c:if test="${!col.isPrimaryKey && col.columnName != 'create_time'}">
                <tr>
                    <td align="right">${col.displayName}:</td>
                    <td>
                        <c:choose>
                            <c:when test="${col.columnName == 'wireless' || col.columnName == 'noise_cancelling'}">
                                <select name="${col.columnName}">
                                    <option value="1">是</option>
                                    <option value="0">否</option>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <input type="text" name="${col.columnName}" required>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        <tr>
            <td colspan="2" align="center">
                <input type="submit" value="保存">
                <a href="${pageContext.request.contextPath}/${entityName}?action=list">
                    <input type="button" value="返回">
                </a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>