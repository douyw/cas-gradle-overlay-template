<%--@elvariable id="appUrl" type="java.lang.String"--%>
<%--@elvariable id="username" type="java.lang.String"--%>
<%--@elvariable id="appName" type="java.lang.String"--%>
<%--@elvariable id="userApplications" type="java.util.List"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:set var="viewAccessTokenUrl" value="${pageContext.request.contextPath}/app/mashery/viewAccessToken"/>
<c:set var="revokeUrl" value="${pageContext.request.contextPath}/app/mashery/revokeAccessToken"/>
<c:set var="userApplicationSearchJs" value="${pageContext.request.contextPath}/js/userApplicationSearch.js"/>

<html>
<head>
    <title><spring:message code="search.mashery.user.applications.label"/></title>
    <meta name="decorator" content="central"/>
</head>
<body>

<div class="panel panel-default">
    <div class="panel-heading">
        <form:form id="formSearch" class="">
            <input type="hidden" id="appName" name="appName" class="form-control" value="${appName}">
            <input type="hidden" id="username" name="username" class="form-control" value="${username}">

            <div class="form-group">
                <input type="text" id="search" name="search" class="typeahead form-control" placeholder="ISID or Account">
            </div>
        </form:form>
    </div>
    <div class=" panel-body table-responsive">
        <c:if test="${!empty userApplications}">
            <div id="userAccount" class="form-group well">
                <label>User Account</label>

                <p id="usernameLabel" class="help-block">${username}</p>

                <p id="appUrl" class="help-block">${appUrl}</p>
            </div>

            <table class="table" id="userTable">
                <thead>
                <tr>
                    <th>
                        <spring:message code="mashery.name.label"/>
                    </th>
                    <th>
                        <spring:message code="view.mashery.action.label"/>
                    </th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="userApplication" items="${userApplications}">
                    <tr>
                        <td>
                            <a href="#" rel="popover" data-toggle="popover" data-placement="bottom" title="Access Tokens">
                                    ${userApplication.name}
                            </a>

                            <div class="hidden">
                                <ul class="list-unstyled">
                                    <c:forEach var="token" items="${userApplication.accessTokens}" varStatus="status">
                                        <li>
                                            <a href="${viewAccessTokenUrl}?accessToken=${token}&appName=${appName}&username=${username}">${token}</a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </td>
                        <td>
                            <form:form role="form" action="${revokeUrl}">
                                <input type="hidden" name="username" value="${username}">
                                <input type="hidden" name="appName" value="${appName}">
                                <input type="hidden" name="clientId" value="${userApplication.clientId}">

                                <button type="submit" class="btn btn-sm btn-danger">Revoke</button>
                            </form:form>
                        </td>
                    </tr>
                </c:forEach>

                </tbody>
            </table>
        </c:if>
        <c:if test="${empty userApplications}">
            <p>No Access Tokens Found</p>
        </c:if>
    </div>
</div>

<content tag="local_script">
    <script type="text/javascript" src="${userApplicationSearchJs}"></script>
</content>

</body>
</html>