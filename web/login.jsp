<%--
  Created by IntelliJ IDEA.
  User: artemz
  Date: 1/4/14
  Time: 3:01 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<head>
    <title>Login page</title>
</head>
<body>
<h2>Login</h2>
<shiro:guest>
    Hi, guest!
</shiro:guest>
<form version="" method="post">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username">
    <br>
    <label for="password">Password:</label>
    <input type="text" id="password" name="password">
    <br>
    <label for="rememberMe">Remember me:</label>
    <input type="checkbox" id="rememberMe" name="rememberMe" value="true" />
    <br/>
    <input type="submit" value="Login" />
    <%--<span class="error">#{shiroLoginFailure}</span>--%>

</form>

</body>
</html>