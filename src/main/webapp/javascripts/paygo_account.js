var Account = Account || {};
$(function () {
    Account = {
        // URL
        requestURL: '/reportServlet/createAccount',
        googleSignInURL: '/reportServlet/googleSignIn',
        userData: {},
        // authorization
        logIn: function (opt) {
            $.ajax({
                url: Account.requestURL,
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                processData: false,
                data: JSON.stringify({
                    action: 'logIn',
                    email: opt.email,
                    password: opt.password
                }),
                complete: opt.complete
            });
        },
        // signing up
        signUp: function (opt) {
            $.ajax({
                url: Account.requestURL,
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                processData: false,
                data: JSON.stringify({
                    action: 'signUp',
                    email: opt.email,
                    firstName: opt.firstName,
                    lastName: opt.lastName,
                    password: opt.password
                }),
                complete: opt.complete
            });
        },
        googleSignIn: function (opt) {
            $.ajax({
                url: Account.googleSignInURL,
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                processData: false,
                data: JSON.stringify({
                    idToken: opt.id_token,
                }),
                complete: opt.complete
            });
        },
        // logout
        logoutRequestURL: '/reportServlet/logout',
        userData: {},
        logOut: function () {
            $.ajax({
                url: Account.logoutRequestURL,
                method: 'POST'
            });
            Cookies.remove('userData');
            Account.userData = {};
            location.href = '/index.html';
        },
        getUserInfoRequestURL: '/reportServlet/getUserInfo',
        userData: {},
        getUserInfo: function (opt) {
            $.ajax({
                url: Account.getUserInfoRequestURL,
                method: 'POST',
                success: opt.complete
            });
        },
        getCookies: function () {
            Account.userData = Cookies.getJSON('userData');
            if (Account.userData) {
                $('#dropdownMenuUserEmail').html(Account.userData.email);
                $('#sendToUserEmail').html(Account.userData.email);
                $("#loginButton").hide();
                $("#signUpButton").hide();
                $("#dropdownBlock").show();
                $('#dropdownMenuUserLogOut').click(function () {
                    Account.logOut();
                })
            }
            else {
                $("#loginButton").show();
                $("#signUpButton").show();
                $("#dropdownBlock").hide();
            }
        }
    };
    Account.getCookies();
});
