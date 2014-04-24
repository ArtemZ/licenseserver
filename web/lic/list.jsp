<%--
  Created by IntelliJ IDEA.
  User: artemz
  Date: 1/8/14
  Time: 1:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.net.InetAddress" %>
<%@ page import="net.netdedicated.domain.License" %>
<html>
<head>
    <title>License list</title>
</head>
<body>
<c:choose>
    <c:when test="${licenses != null && licenses.size() > 0}">
        <table border="1">
            <thead>
            <th>#</th>
            <th>IP</th>
            <th>Type</th>
            </thead>
            <c:forEach var="license" items="${licenses}">
                <% String ip = InetAddress.getByAddress(((License)pageContext.getAttribute("license")).getIp()).getHostAddress(); %>
                <tr>
                    <td>${license.id}</td>
                    <td><%=ip%></td>
                    <%--<td>${InetAddress.getByAddress(license.ip).getHostAddress()}</td>--%>
                    <td>${license.type}</td>
                </tr>
            </c:forEach>

        </table>
    </c:when>
    <c:otherwise>
        No licenses found
    </c:otherwise>
</c:choose>
</body>
</html>