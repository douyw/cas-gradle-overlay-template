function wrapCriteriaPart(skipWrap, message) {
    var newMessage;

    if (!skipWrap) {
        newMessage = '<strong class="has-error">' + message + '</strong>';
    } else {
        newMessage = message;
    }

    return newMessage;
}

$(document).ready(function () {

    jQuery.validator.addMethod("password", function (value, element) {
        return this.optional(element) || checkPassword(value);
    }, "Doesn't meet the criteria.");

    //Validate the form
    $('#resetPasswordForm').validate(
        {
            errorElement: "span",
            errorClass: "help-block",
            errorPlacement: function (error, element) {
                element.after('<span class="is-icon is-icon-error form-control-feedback"><img src="/infusionsoft/img/ic-message-danger.svg" width="4" height="16"></span>');
                element.after(error);
            },
            rules: {
                password1: {
                    required: true,
                    password: true,
                    remote: {
                        url: "checkPasswordForLast4WithRecoveryCode",
                        type: "post",
                        data: {
                            recoveryCode: function () {
                                return $("#recoveryCode").val();
                            }
                        }
                    }
                },
                password2: {
                    required: true,
                    password: false,
                    equalTo: "#password1"
                }
            },
            messages: {
                password1: {
                    required: "Password is required.",
                    password: function (params, element) {
                        var currPass = $(element).val();

                        var mobileMessage = '' +
                            wrapCriteriaPart(checkPasswordLength(currPass), 'At least 7 characters') + ', ' +
                            wrapCriteriaPart(checkPasswordNumber(currPass), '1 number') + ', ' +
                            wrapCriteriaPart(checkPasswordUpperCase(currPass), '1 uppercase letter') + ', ' +
                            wrapCriteriaPart(checkPasswordLowerCase(currPass), '1 lowercase letter');

                        return '<span class="mob-help-block">' + mobileMessage + '</span>';
                    },
                    remote: "Password must not match any of your last 4 passwords."
                },
                password2: {
                    required: "Password is required.",
                    equalTo: "Passwords don't match."
                }
            },
            highlight: function (element) {
                //element is the input failing validation

                //Unhide error message
                $(element).next().removeClass("hidden");

                $(element).closest('.form-group').addClass('has-error has-feedback');
                $(element).closest('.form-group').removeClass('has-success');

                //add span icon error classes
                $(element).next().next().addClass('is-icon is-icon-error form-control-feedback');
                $(element).next().next().html('<img src="/infusionsoft/img/ic-message-danger.svg" width="4" height="16">');

                //remove span icon success classes
                $(element).next().next().removeClass('is-icon-ok');
            },
            success: function (element) {
                //element is the error element created by the framework

                $(element).closest('.form-group').addClass('has-success has-feedback');
                $(element).closest('.form-group').removeClass('has-error');

                //Hide error message
                $(element).addClass("hidden");

                //remove span icon error classes
                $(element).next().removeClass('is-icon-error form-control-feedback');
                $(element).next().html('<img src="/infusionsoft/img/ic-message-success.svg" width="16" height="16">');

                //add span icon success classes
                $(element).next().addClass('is-icon-ok form-control-feedback');
            }

        });

});