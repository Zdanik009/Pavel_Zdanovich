<%@ page contentType="text/html" errorPage="jsp/error.jsp" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <fmt:setLocale value="${pageContext.session.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.index"/></title>
</head>
<body>
<jsp:forward page="controller">
    <jsp:param name="command" value="prepare_page"/>
    <jsp:param name="language" value="en_US"/>
</jsp:forward>
</body>
</html>
