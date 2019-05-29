<%@ page contentType="text/html" errorPage="error.jsp" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.playground"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/blank.css"/>
    <script src="${pageContext.request.contextPath}/js/loader.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/timer.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/player.js" type="text/javascript"></script>
</head>
<body>

<form id="playground" method="get" action="controller">
    <input id="command" name="command" type="hidden"/>
    <input id="rout" name="rout" type="hidden"/>
</form>

<h1><fmt:message bundle="${content}" key="label.${pageContext.servletContext.getAttribute('tour').getTitle()}"/></h1>

<c:if test="${pageContext.servletContext.getAttribute('tour_status') == 'submitted'}">
    <h1>
        <fmt:message bundle="${content}" key="label.result"/> : ${pageContext.request.getAttribute('result')}
    </h1>
</c:if>

<div id="timer">00:${pageContext.servletContext.getAttribute('tour').getTime()}:00</div>

<div id="content-pane">
    <fmt:message bundle="${content}" key="text.${pageContext.servletContext.getAttribute('tour').getTitle()}"/>
    <fmt:message bundle="${content}" key="text.rules"/> ${pageContext.servletContext.getAttribute('tour').getTime()}
    <fmt:message bundle="${content}" key="text.minutes"/>.
</div>

<c:choose>
    <c:when test="${pageContext.servletContext.getAttribute('tour_status') == 'loaded'}">
        <button onclick=" document.getElementById('command').value = 'start_tour';
                          document.getElementById('playground').submit();">
            <fmt:message bundle="${content}" key="button.start"/>
        </button>
    </c:when>
    <c:when test="${pageContext.servletContext.getAttribute('tour_status') == 'started'}">
        <script>startTimer()</script>
        <div id="table">
            <jsp:include page="../${pageContext.servletContext.getAttribute('tour').getBlank()}">
                <jsp:param name="tour" value="${pageContext.servletContext.getAttribute('tour')}"/>
                <jsp:param name="tour_status" value="${pageContext.servletContext.getAttribute('tour_status')}"/>
            </jsp:include>
        </div>
        <button onclick=" document.getElementById('command').value = 'submit_tour';
                          document.getElementById('playground').submit();">
            <fmt:message bundle="${content}" key="button.submit"/>
        </button>
    </c:when>
    <c:when test="${pageContext.servletContext.getAttribute('tour_status') == 'submitted'}">
        <div id="table">
            <jsp:include page="../${pageContext.servletContext.getAttribute('tour').getBlank()}">
                <jsp:param name="tour" value="${pageContext.servletContext.getAttribute('tour')}"/>
                <jsp:param name="tour_status" value="${pageContext.servletContext.getAttribute('tour_status')}"/>
                <jsp:param name="answers" value="${pageContext.request.getAttribute('answers')}"/>
            </jsp:include>
        </div>
        <c:choose>
            <c:when test="${pageContext.servletContext.getAttribute('game_status') == 'loaded'}">
                <button onclick=" document.getElementById('command').value = 'next_tour';
                                  document.getElementById('playground').submit();">
                    <fmt:message bundle="${content}" key="button.next"/>
                </button>
            </c:when>
            <c:when test="${pageContext.servletContext.getAttribute('game_status') == 'finished'}">
                <button onclick=" document.getElementById('rout').value = 'jsp/user_statistic.jsp';
                                  document.getElementById('command').value = 'leave_playground';
                                  document.getElementById('playground').submit();">
                    <fmt:message bundle="${content}" key="label.user"/>
                    <fmt:message bundle="${content}" key="label.statistic"/>
                </button>
            </c:when>
        </c:choose>
        <c:if test="${pageContext.servletContext.getAttribute('tour').getTitle() == 'random' && pageContext.servletContext.getAttribute('user') == null}">
            <button onclick="location.href='${pageContext.request.contextPath}/jsp/login.jsp';">
                <fmt:message bundle="${content}" key="label.login"/>
            </button>
        </c:if>
    </c:when>
</c:choose>

<button onclick=" document.getElementById('rout').value = 'jsp/main.jsp';
                  document.getElementById('command').value = 'leave_playground';
                  document.getElementById('playground').submit();">
    <fmt:message bundle="${content}" key="label.main"/>
</button>

</body>
</html>
