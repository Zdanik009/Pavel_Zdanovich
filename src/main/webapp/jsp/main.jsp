<%@ page contentType="text/html" errorPage="error.jsp" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>

    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.melotrack"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css"/>
    <script src="${pageContext.request.contextPath}/js/example.js" type="text/javascript"></script>

</head>
<body>

<jsp:include page="navigation.jsp"/>

<img alt="logo" src="${pageContext.request.contextPath}/image/logo.png">

<div id="content-pane">

    <fmt:message bundle="${content}" key="text.specification_start"/>

    <audio id="example_player" oncanplaythrough="example_control('example_player')" controls>
        <source src="${pageContext.request.contextPath}/example/04_rick_astley_never_gonna_give_you_up_myzuka.mp3">
    </audio>

    <label for="example_input">
        <fmt:message bundle="${content}" key="label.input"/>
    </label>
    <input id="example_input" name="example_input" formmethod="get" formaction="controller">

    <fmt:message bundle="${content}" key="text.specification_end"/>

</div>

</body>
</html>
