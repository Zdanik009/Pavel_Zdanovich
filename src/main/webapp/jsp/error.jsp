<%@ page contentType="text/html" isErrorPage="true" isELIgnored="false" %>
<%@ page isErrorPage="true" import="java.io.*" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en" >
<br>
<head>
    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.error"/></title>
</head>
<body>

<h1>Request from ${pageContext.errorData.requestURI} is failed</h1><br>
<h1>Servlet name: ${pageContext.errorData.servletName}</h1><br>
<h1>Status code: ${pageContext.errorData.statusCode}</h1><br>
<h1>Exception: ${pageContext.exception}</h1><br>
<h1>Message from exception: ${pageContext.exception.message}</h1><br>


<a href="controller?command=show_main_page">
    <fmt:message bundle="${content}" key="label.main"/>
</a>

</body>
</html>
