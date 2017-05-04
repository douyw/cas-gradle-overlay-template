<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>

<%--@elvariable id="appUrl" type="java.lang.String"--%>
<%--@elvariable id="user" type="org.apereo.cas.infusionsoft.domain.User"--%>

<c:set var="centralUrl" value="${pageContext.request.contextPath}/app/central/home"/>
<c:set var="imageUrl" value="${pageContext.request.contextPath}/images/incognito.png"/>

<c:choose>
    <c:when test="${not empty appUrl}">
        <c:set var="nextUrl" value="${appUrl}"/>
    </c:when>
    <c:otherwise>
        <c:set var="nextUrl" value="${centralUrl}"/>
    </c:otherwise>
</c:choose>

<head>
    <meta name="decorator" content="black-header-minimal"/>
    <meta name="robots" content="noindex">
</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-xs-12 col-sm-6 col-sm-offset-3 col-lg-4 col-lg-offset-4">

            <h3 class="text-center">
                This Is Your Infusionsoft ID!
            </h3>

            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="well well-sm">
                        <div class="row">
                            <div class="col-xs-3">
                                <versioned:img src="${imageUrl}"/>
                            </div>
                            <div class="col-xs-9">
                                <h4>${user.firstName} ${user.lastName}</h4>
                                ${user.username}
                            </div>
                        </div>

                    </div>

                    <p>
                        You will sign in to your accounts with this email address and password.
                    </p>

                    <p>
                        <a class="btn btn-primary pull-right" href="${nextUrl}" role="button">Done</a>
                    </p>
                </div>
            </div>
        </div>
    </div>


</div>

</body>