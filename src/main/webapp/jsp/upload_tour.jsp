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
            "${tour.title}" <fmt:message bundle="${content}" key="label.uploaded"/>
        </h1>
    </c:when>
    <c:when test="${upload_status == 'failed'}">
        <h1>
            <fmt:message bundle="${content}" key="label.status"/>:
            <fmt:message bundle="${content}" key="label.failed"/>
        </h1>
    </c:when>
</c:choose>

<div><fmt:message bundle="${content}" key="text.upload_tour"/></div>

<form id="upload" method="get" action="controller">
    <input id="command" name="command" type="hidden" value="upload_tour">
    <label for="tour_title">
        <fmt:message bundle="${content}" key="label.tour"/>
        <fmt:message bundle="${content}" key="label.title"/>:
    </label>
    <input id="tour_title" name="tour_title" type="text" required="required"/>
    <c:forEach var="i" begin="0" end="9">
        <label for="song_title_${i}">
            <fmt:message bundle="${content}" key="label.song"/> № ${i + 1} :
        </label>
        <select id="song_title_${i}" name="song_title_${i}" required="required">
            <c:forEach items="${pageContext.servletContext.getAttribute('songs_titles')}" var="song_title">
                <option>${song_title}</option>
            </c:forEach>
        </select>
    </c:forEach>
    <label for="time">
        <fmt:message bundle="${content}" key="label.time"/>:
    </label>
    <input id="time" name="time" type="number" required="required"/>
    <fmt:message bundle="${content}" key="button.submit" var="submit"/>
    <input id="submit" name="submit" type="submit" value="${submit}" required="required"/>
</form>

</body>
</html>
