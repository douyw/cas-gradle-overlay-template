<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<meta name="decorator" content="green-header-minimal"/>

<style type="text/css">
    #recover {
        width: 299px;
        margin: 10px auto;
    }

    #back-to-signin {
        text-align: center;
        margin: 5px auto;
    }
    input[type="text"] {
        padding: 6px 6px;
    }
</style>

<script type="text/javascript">

    $(document).ready(function() {
        jQuery.validator.addMethod("password", function( value, element ) {
            var result = this.optional(element) || value.length >= 7 && /\d/.test(value) && /[a-z]/.test(value) && /[A-Z]/.test(value);
            return result;
        }, "<spring:message code='registration.error.invalidPassword'/>");

        //Check to ensure that password matches
        //Validate the form
        $('#fm1').validate(
                {
                    rules: {
                        password1: {
                            required: true,
                            password: true
                        },
                        password2: {
                            required: true,
                            password: false,
                            equalTo: "#password1"
                        }
                    },
                    messages: {
                        password1: {
                            required:  "<spring:message code='registration.error.passwordsRequired'/>",
                            password: "<spring:message code='registration.error.invalidPassword'/>"
                        },
                        password2: {
                            required:  "<spring:message code='registration.error.passwordsRequired'/>",
                            equalTo: "<spring:message code='registration.error.passwordsNoMatch'/>"
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

<div id="top-spacer" style="height: 101px;"></div>
<div id="recover">
    <div class="top-blue">
        Create New Password
    </div>
    <div class="bottom-brown">
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <spring:message code="${error}"/>
            </div>
        </c:if>

        <form action="reset" method="post" id="fm1" class="form-vertical">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="password1">New Password</label>
                    <div class="controls">
                        <input id="password1" name="password1" value="" type="password" style="width: 233px"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password2">Retype Password</label>
                    <div class="controls">
                        <input id="password2" name="password2" value="" type="password" style="width: 233px"/>
                    </div>
                </div>
            </fieldset>

            <input name="recoveryCode" type="hidden" value="${fn:escapeXml(recoveryCode)}"/>

            <div class="control-group" style="text-align: right">
                <input class="btn btn-primary" name="submit" accesskey="l" value="Change Password" tabindex="4" type="submit" />
            </div>
        </form>
    </div>
</div>

<div id="back-to-signin">
    <c:url var="loginUrl" value="/login"/>
    <a href="${loginUrl}">Back to Sign In</a>
</div>