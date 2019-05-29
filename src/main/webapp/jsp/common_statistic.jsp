<%@ page contentType="text/html" errorPage="error.jsp" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>

    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.statistic"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css"/>

</head>
<body>

<jsp:include page="navigation.jsp"/>

<table>
    <tr>
        <th>№</th>
        <th><fmt:message bundle="${content}" key="label.user"/></th>
        <th>
            <fmt:message bundle="${content}" key="label.total"/>
            <fmt:message bundle="${content}" key="label.result"/>
        </th>
    </tr>
    <c:forEach var="i" begin="0" end="${fn:length(pageContext.request.getAttribute('leader_names')) - 1}">
        <tr>
            <td>${i + 1}</td>
            <td>${pageContext.request.getAttribute('leader_names')[i]}</td>
            <td>${pageContext.request.getAttribute('leader_results')[i]}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
