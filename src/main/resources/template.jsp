<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <fmt:setLocale value="${pageContext.servletContext.getAttribute('language')}"/>
    <fmt:setBundle basename="content" var="content"/>
    <title><fmt:message bundle="${content}" key="label.uploaded"/></title>
</head>
<body>
<table>
    <thead>
    <tr>
        <th>
            <h1></h1>
        </th>
        <th>
            <h1>
                <fmt:message bundle="${content}" key="label.musician"/>
            </h1>
        </th>
        <th>
            <h1>
                <fmt:message bundle="${content}" key="label.title"/>
            </h1>
        </th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="i" begin="0" end="${tour.getDefaultTourSongsAmount() - 1}">
        <tr>
            <td>
                <audio id="player_${i}" oncanplaythrough="control('player_${i}', '${tour_status}', ${tour.songs[i].getStartTime()}, ${tour.songs[i].isEnable()})" controls>
                    <source src="${tour.songs[i].audio}"/>
                </audio>
            </td>
            <td>
                <c:choose>
                    <c:when test="${tour_status == 'started'}">
                        <input id="musician_${i}" name="musician_${i}" type="text" form="playground"/>
                    </c:when>
                    <c:when test="${tour_status == 'submitted'}">
                        <div class="content">${tour.songs[i].musician}</div>
                    </c:when>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when test="${tour_status == 'started'}">
                        <input id="title_${i}" name="title_${i}" type="text" form="playground"/>
                    </c:when>
                    <c:when test="${tour_status == 'submitted'}">
                        <div class="content">${tour.songs[i].title}</div>
                    </c:when>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>