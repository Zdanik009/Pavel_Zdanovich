<%@ page contentType="text/html" errorPage="error.jsp" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.upload"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/upload.css">
</head>
<body>

<jsp:include page="navigation.jsp"/>

<h1><fmt:message bundle="${content}" key="label.upload"/></h1>

<c:choose>
    <c:when test="${upload_status == 'uploaded'}">
        <h1>
            <fmt:message bundle="${content}" key="label.status"/>:
            "${game.title}" <fmt:message bundle="${content}" key="label.uploaded"/>
        </h1>
    </c:when>
    <c:when test="${upload_status == 'failed'}">
        <h1>
            <fmt:message bundle="${content}" key="label.status"/>:
            <fmt:message bundle="${content}" key="label.failed"/>
        </h1>
    </c:when>
</c:choose>

<div id="content-pane"><fmt:message bundle="${content}" key="text.upload_game"/></div>

<form id="upload" method="get" action="controller">
    <input id="command" name="command" type="hidden" value="upload_game"/>
    <label for="game_title">
        <fmt:message bundle="${content}" key="label.game"/>
        <fmt:message bundle="${content}" key="label.title"/>:
    </label>
    <input id="game_title" name="game_title" type="text" required="required"/>
    <c:forEach var="i" begin="0" end="2">
        <label for="tour_${i}">
            <fmt:message bundle="${content}" key="label.tour"/> № ${i + 1} :
        </label>
        <select id="tour_title_${i}" name="tour_title_${i}" required="required">
            <c:forEach items="${pageContext.servletContext.getAttribute('tours_titles')}" var="tour_title">
                <option>${tour_title}</option>
            </c:forEach>
        </select>
    </c:forEach>
    <fmt:message bundle="${content}" key="button.submit" var="submit"/>
    <input id="submit" name="submit" type="submit" value="${submit}" required="required"/>
</form>

</body>
</html>
