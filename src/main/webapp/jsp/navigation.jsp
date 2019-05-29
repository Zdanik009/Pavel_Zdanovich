<%@ page contentType="text/html" errorPage="error.jsp" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>

    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.navigation"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navigation.css"/>

</head>
<body>

<nav role='navigation'>
    <ul class="right-buttons">
        <li class="buttonMain">
            <a href="controller?command=show_main_page">
                <fmt:message bundle="${content}" key="label.main"/>
            </a>
        </li>
        <li class="buttonPlay">
            <a href="#"><fmt:message bundle="${content}" key="label.play"/></a>
            <ul>
                <li>
                    <a href="#"><fmt:message bundle="${content}" key="label.game"/></a>
                    <ul>
                        <c:forEach var="game_title" items="${pageContext.servletContext.getAttribute('game_titles')}">
                            <li>
                                <a href="#" onclick="if(${pageContext.servletContext.getAttribute('user') == null}) {
                                            location.href='controller?command=show_login_page'; } else {
                                            location.href='controller?command=load_game&game_title=${game_title}';}">
                                        <fmt:message bundle="${content}" key="label.${game_title}"/>
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </li>
                <li>
                    <a href="controller?command=load_tour&tour_title=random">
                        <fmt:message bundle="${content}" key="label.random"/>
                        <fmt:message bundle="${content}" key="label.tour"/>
                    </a>
                </li>
            </ul>
        </li>
        <c:if test="${pageContext.servletContext.getAttribute('user').getRole() == 'admin'}">
            <li class="buttonUpload">
                <a href="#"><fmt:message bundle="${content}" key="label.upload"/></a>
                <ul>
                    <li>
                        <a href="controller?command=show_upload_game_page">
                            <fmt:message bundle="${content}" key="label.game"/>
                        </a>
                    </li>
                    <li>
                        <a href="controller?command=show_upload_tour_page">
                            <fmt:message bundle="${content}" key="label.tour"/>
                        </a>
                    </li>
                    <li>
                        <a href="controller?command=show_upload_song_page">
                            <fmt:message bundle="${content}" key="label.song"/>
                        </a>
                    </li>
                </ul>
            </li>
        </c:if>
        <li class="buttonStatistic">
            <a href="#"><fmt:message bundle="${content}" key="label.statistic"/></a>
            <ul>
                <li>
                    <a href="#" onclick="if (${pageContext.servletContext.getAttribute('user') == null}) {
                                         location.href='controller?command=show_login_page'; } else {
                                         location.href='controller?command=load_common_statistic';}">
                        <fmt:message bundle="${content}" key="label.common"/>
                    </a>
                </li>
                <li>
                    <a href="#" onclick="if (${pageContext.servletContext.getAttribute('user') == null}) {
                                         location.href='controller?command=show_login_page'; } else {
                                         location.href='controller?command=load_user_statistic';}">
                        <fmt:message bundle="${content}" key="label.user"/>
                    </a>
                </li>
            </ul>
        </li>
        <li class="buttonAbout">
            <a href="controller?command=show_about_page">
                <fmt:message bundle="${content}" key="label.about"/>
            </a>
        </li>
    </ul>
    <ul class="left-buttons">
        <li class="buttonLogin">
            <c:choose>
                <c:when test="${pageContext.servletContext.getAttribute('user') == null}">
                    <a href="controller?command=show_login_page">
                        <fmt:message bundle="${content}" key="label.login"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="controller?command=logout_user">
                        <fmt:message bundle="${content}" key="label.logout"/>
                    </a>
                </c:otherwise>
            </c:choose>
        </li>
        <li class="buttonLanguage">
            <a href="#"><fmt:message bundle="${content}" key="label.language"/></a>
            <ul>
                <li>
                    <a href="controller?command=change_language&language=en_US">EN</a>
                </li>
                <li>
                    <a href="controller?command=change_language&language=be_BY">BY</a>
                </li>
                <li>
                    <a href="controller?command=change_language&language=ru_BY">RU</a>
                </li>
            </ul>
        </li>
    </ul>
</nav>

</body>
</html>
