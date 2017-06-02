//Password check stuff
function checkPasswordLength(currPass) {
    return currPass.length >= 7;
}

function checkPasswordNumber(currPass) {
    return /\d/.test(currPass);
}

function checkPasswordUpperCase(currPass) {
    return /[A-Z]/.test(currPass);
}

function checkPasswordLowerCase(currPass) {
    return /[a-z]/.test(currPass);
}

function checkPassword(currPass) {
    return checkPasswordLength(currPass) && checkPasswordNumber(currPass) && checkPasswordLowerCase(currPass) && checkPasswordUpperCase(currPass)
}

$(document).ready(function () {
    var showHidePassword = document.getElementById('showHidePassword');
    if (showHidePassword) {
        showHidePassword.addEventListener("click", togglePasswordVisible);
    }
});

function togglePasswordVisible() {
    var password = document.getElementById("password");
    if (password.type === 'text') {
        $("#showPassword").css({display: ''});
        $("#showPasswordLabel").css({display: ''});
        $("#hidePassword").css({display: 'none'});
        $("#hidePasswordLabel").css({display: 'none'});
        password.type = 'password';
    } else {
        $("#showPassword").css({display: 'none'});
        $("#showPasswordLabel").css({display: 'none'});
        $("#hidePassword").css({display: ''});
        $("#hidePasswordLabel").css({display: ''});
        password.type = 'text';
    }
}

