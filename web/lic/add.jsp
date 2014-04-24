<%--
  Created by IntelliJ IDEA.
  User: artemz
  Date: 1/6/14
  Time: 6:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Add License</title>
</head>
<body>
<c:if test="${errorBean != null}">
    <div version="" class="error">
        ${errorBean.errorMessage}
    </div>
</c:if>
    <form action="${pageContext.request.contextPath}/license" method="POST">
        <p>
            <label>IP Address:</label>
            <input type="text" name="ipaddr" />
        </p>
        <p>
            <label>License Type:</label>
            <input type="text" name="type" />
        </p>
        <input type="submit" value="Submit"/>
    </form>
</body>
</html>