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
            "${song.musician} - ${song.title}" <fmt:message bundle="${content}" key="label.uploaded"/>
        </h1>
    </c:when>
    <c:when test="${upload_status == 'failed'}">
        <h1>
            <fmt:message bundle="${content}" key="label.status"/>:
            <fmt:message bundle="${content}" key="label.failed"/>
        </h1>
    </c:when>
</c:choose>

<div id="content-pane"><fmt:message bundle="${content}" key="text.upload_song"/></div>

<form id="upload" method="post" enctype="multipart/form-data" action="controller">
    <input id="command" name="command" type="hidden" value="upload_song"/>
    <label for="musician">
        <fmt:message bundle="${content}" key="label.musician"/>:
    </label>
    <input id="musician" name="musician" type="text" required="required"/>
    <label for="title">
        <fmt:message bundle="${content}" key="label.title"/>:
    </label>
    <input id="title" name="title" type="text" required="required"/>
    <label for="genre">
        <fmt:message bundle="${content}" key="label.genre"/>:
    </label>
    <input id="genre" name="genre" type="text"/>
    <label for="album">
        <fmt:message bundle="${content}" key="label.album"/>:
    </label>
    <input id="album" name="album" type="text"/>
    <label for="date">
        <fmt:message bundle="${content}" key="label.date"/>:
    </label>
    <input id="date" name="date" type="text"/>
    <label for="information">
        <fmt:message bundle="${content}" key="label.information"/>:
    </label>
    <input id="information" name="information" type="text"/>
    <fmt:message bundle="${content}" var="file_label" key="label.file"/>
    <input id="file" name="file" type="file" accept="audio/mpeg, audio/mp3" required="required"/>
    <fmt:message bundle="${content}" key="button.submit" var="submit"/>
    <input id="submit" name="submit" type="submit" value="${submit}" required="required"/>
</form>

</body>
</html>
