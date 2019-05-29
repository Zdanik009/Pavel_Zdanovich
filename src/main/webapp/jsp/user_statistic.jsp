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

<c:choose>
    <c:when test="${pageContext.servletContext.getAttribute('user_statistic') != null}">
        <table>
            <tr>
                <th>
                    <fmt:message bundle="${content}" key="label.tour"/>
                    (<fmt:message bundle="${content}" key="label.game"/>)
                </th>
                <th><fmt:message bundle="${content}" key="label.date"/></th>
                <th><fmt:message bundle="${content}" key="label.result"/></th>
            </tr>
            <c:forEach var="i" begin="0" end="${pageContext.servletContext.getAttribute('user_statistic').size() - 1}">
                <tr>
                    <td>${pageContext.servletContext.getAttribute('game_tours_titles')[i]}</td>
                    <td>${pageContext.servletContext.getAttribute('user_statistic').dates[i]}</td>
                    <td>${pageContext.servletContext.getAttribute('user_statistic').results[i]}</td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        <h1>
            <fmt:message bundle="${content}" key="label.statistic"/>
            <fmt:message bundle="${content}" key="label.error"/>
        </h1>
    </c:otherwise>
</c:choose>

</body>
</html>
