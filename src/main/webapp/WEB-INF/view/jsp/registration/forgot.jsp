<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<meta name="decorator" content="anonymous"/>

<style type="text/css">

    #forgot {
        color: #444;
        background: #fff;
        width: 280px;
        margin: 10px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 30px;
    }

    #back-to-signin {
        width: 336px;
        margin: 5px auto;
        text-align: center;
    }

</style>

<script type="text/javascript">

    $(document).ready(function() {

        //Validate the form
        $('#fm1').validate(
                {
                    rules: {
                        username: {
                            required: true,
                            email: true
                        }
                    },
                    messages: {
                        username: {
                            required: "<spring:message code='forgotpassword.noUsername'/>",
                            email: "<spring:message code='registration.error.invalidEmail'/>"
                        }

                    },
                    highlight: function(element) {
                        $(element).closest('.control-group').removeClass('success').addClass('error');
                    },
                    success: function(element) {
                        element.closest('.control-group').removeClass('error');
                        element.closest('label.error').hide().removeClass('error').addClass('valid').addClass('error');
                    }

                });

    });

</script>

<div id="forgot">
    <c:if test="${not empty error}">
        <div class="alert alert-error" style="margin: -20px -20px 20px -20px">
            <spring:message code="${error}"/>
        </div>
    </c:if>

    <form action="recover" method="post" id="fm1" class="form-vertical">
        <div class="control-group">
            <label for="username" class="control-label">Please <c:if test="${empty username}">enter</c:if><c:if test="${!empty username}">verify</c:if> your email address</label>
            <div class="controls">
                <input type="text" class="required" id="username" name="username" size="25" tabindex="1" style="width: 266px" value="${username}" />
            </div>
        </div>

        <div class="control-group" style="text-align: right">
            <input class="btn btn-primary" name="submit" accesskey="l" value="Next" tabindex="4" type="submit" />
        </div>
    </form>
</div>

<div id="back-to-signin">
    <c:url var="loginUrl" value="/login"/>
    <a href="${loginUrl}">Back to Sign In</a>
</div>