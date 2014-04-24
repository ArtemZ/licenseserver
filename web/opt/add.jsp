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
    <title>Add Option</title>
</head>
<body>
<c:if test="${errorBean != null}">
    <div version="" class="error">
        ${errorBean.errorMessage}
    </div>
</c:if>
    <form action="${pageContext.request.contextPath}/option" method="POST">
        <p>
            <label>Option Key:</label>
            <input type="text" name="key" />
        </p>
        <p>
            <label>Option Value:</label>
            <input type="text" name="value" />
        </p>
        <input type="submit" value="Submit"/>
    </form>
</body>
</html>