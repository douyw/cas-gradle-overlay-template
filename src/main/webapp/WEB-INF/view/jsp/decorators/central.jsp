<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/bootstrap.min.css" />"/>
    <spring:theme code="standard.custom.css.file" var="customCssFile"/>
    <link type="text/css" rel="stylesheet" href="<c:url value="${customCssFile}" />"/>
    <script type="text/javascript" src="<c:url value="/js/jquery-1.7.2.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/bootstrap.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery.jeditable.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery.placeholder.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery.qtip-1.0.0-rc3.min.js"/>"></script>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800" type="text/css">
    <decorator:head/>
</head>
<body>
<div id="headerbg">
    <div id="header">
        <a id="logo" href="<c:url value="/"/>"></a>

        <div id="userinfo">
            <c:url var="logoutUrl" value="/j_spring_security_logout"/>
            Signed in as: <strong><sec:authentication property="principal.firstName"/> <sec:authentication property="principal.lastName"/></strong> |
            <a href="${logoutUrl}">Sign Out</a>
        </div>
        <span id="title">Account Central</span>
    </div>
</div>
<div id="navbg">
    <div id="nav">
        <ul>
            <c:url var="homeLink" value="/app/central/home"/>
            <c:url var="editProfileLink" value="/app/central/editProfile"/>
            <li><a href="${homeLink}" class="${homeLinkSelected}">YOUR ACCOUNTS</a></li>
            <li><a href="${editProfileLink}" class="${editProfileLinkSelected}">EDIT YOUR ID</a></li>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
                <form id="userSearch" class="navbar-search pull-right" action="/app/admin/userSearch">
                    <input type="text" class="search-query" name="searchUsername" placeholder="Search Infusionsoft ID" value="${searchUsername}"/>
                </form>
            </sec:authorize>
        </ul>
    </div>
</div>
<div class="wrapper">
    <div id="content">
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        <c:if test="${not empty info}">
            <div class="alert alert-info">${info}</div>
        </c:if>
        <decorator:body/>
    </div>
</div>
<script type="text/javascript">(function () {
    var walkme = document.createElement('script');
    walkme.type = 'text/javascript';
    walkme.async = true;
    walkme.src = 'https://d3b3ehuo35wzeh.cloudfront.net/users/6543/walkme_6543_https.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(walkme, s);
})();</script>
</body>
</html>
