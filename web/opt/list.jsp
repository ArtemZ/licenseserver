<%--
  Created by IntelliJ IDEA.
  User: artemz
  Date: 1/8/14
  Time: 1:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Option list</title>
</head>
<body>
<c:choose>
    <c:when test="${options != null && options.size() > 0}">
        <table border="1">
            <thead>
            <th>#</th>
            <th>Key</th>
            <th>Value</th>
            </thead>
            <c:forEach var="option" items="${options}">
                <tr>
                    <td>${option.id}</td>
                    <td>${option.key}</td>
                    <td>${option.value}</td>
                </tr>
            </c:forEach>

        </table>
    </c:when>
    <c:otherwise>
        No options found
    </c:otherwise>
</c:choose>
</body>
</html>