<%@ page contentType="text/html" errorPage="error.jsp" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>

    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.about"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css"/>

</head>
<body>

<jsp:include page="navigation.jsp"/>

<h1><fmt:message bundle="${content}" key="label.about"/></h1>

<div id="content-pane">

    <fmt:message bundle="${content}" key="text.description"/>
    <fmt:message bundle="${content}" key="text.game"/>
    <fmt:message bundle="${content}" key="text.tour"/>

</div>

</body>
</html>
