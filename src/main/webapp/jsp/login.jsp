<%@ page contentType="text/html" errorPage="error.jsp" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>

    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.login"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css"/>

</head>
<body>

<form id="login" method="post" action="${pageContext.request.contextPath}/controller">
    <input id="command" name="command" type="hidden">
    <div id="login-label">
        <fmt:message bundle="${content}" key="label.please_login"/>
    </div>
    <label id="username-label" for="username">
        <fmt:message bundle="${content}" key="label.username"/>
    </label>
    <input id="username" name="username" type="text" onFocus="field_focus(this, 'email');"
           onblur="field_blur(this, 'email');" class="email"/>
    <label id="password-label" for="password">
        <fmt:message bundle="${content}" key="label.password"/>
    </label>
    <input id="password" name="password" type="password" onFocus="field_focus(this, 'email');"
           onblur="field_blur(this, 'email');" class="email"/>
    <div id="buttons-box">
        <div id="sign-in-button">
            <a href="#" onclick="document.getElementById('command').value = 'sign_in_user'; document.getElementById('login').submit();">
                <fmt:message bundle="${content}" key="label.sign_in"/>
            </a>
        </div>
        <div id="sign-up-button">
            <a href="#" onclick="document.getElementById('command').value = 'sign_up_user'; document.getElementById('login').submit();">
                <fmt:message bundle="${content}" key="label.sign_up"/>
            </a>
        </div>
    </div>
</form>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/login.js"></script>

</body>
</html>
