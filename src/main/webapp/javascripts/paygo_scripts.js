$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip();
    $('.empty-cart').hide();
    $('#delete-modal').on('show', function () {
        var id = $(this).closest('tr').data('id'),
            removeBtn = $(this).find('.danger');
    });
    $('#btnYes').click(function () {
        var id = $('#delete-modal').data('id');
        $('[data-id=' + id + ']').remove();
        $('#delete-modal').modal('hide');
        var items = $('#checkout-table tbody').children().length;
        if (items == 0) {
            $('.empty-cart').show();
        }
    });

    // add submit handlers on login/register button and on enter
    $("#login-submit-modal").click(login_submit_handler);
    $("form#logIn-form :input").each(function () {
        var input = $(this); // This is the jquery object of the input
        input.keydown(function (event) {
            if (event.keyCode == 13) {
                login_submit_handler(event);
            }
        });
    });

    $("#register-submit-modal").click(register_submit_handler);
    $("form#registration-form :input").each(function () {
        var input = $(this); // This is the jquery object of the input
        input.keydown(function (event) {
            if (event.keyCode == 13) {
                register_submit_handler(event);
            }
        });
    });



// submit on button "Register" and on enter
    function register_submit_handler(event) {
        event.preventDefault();
        var email = $('#register-email-modal').val(),
            firstName = $('#register-first-name-modal').val(),
            lastName = $('#register-last-name-modal').val(),
            password = $('#register-password-modal').val(),
            retypePassword = $('#register-retype-password-modal').val();
        // form validation
        if (email.match(/^[\w\-\.]+@[\w\-\.]+\.\w+$/) && firstName && lastName && password && password === retypePassword) {
            // user signing up
            Account.signUp({
                email: email,
                firstName: firstName,
                lastName: lastName,
                password: password,
                complete: function (response) {
                    var json = response.responseJSON;
                    if (json.error) {
                        alert(json.error);
                    } else {
                        Cookies.set('userData', json, {expires: 30});
                        $('#signUp').modal('hide');
                        Account.getCookies();
                    }
                }
            });
        } else {
            alert('Please enter all fields');
        }
        return true;
    }


// submit on button "Login" and on enter
    function login_submit_handler(event) {
        event.preventDefault();
        var email = $('#login-email-modal').val(),
            password = $('#login-password-modal').val();
        // if email and password exist
        if (email.match(/^[\w\-\.]+@[\w\-\.]+\.\w+$/) && password) {
            // user authorizing
            Account.logIn({
                email: email,
                password: password,
                complete: function (response) {
                    var json = response.responseJSON;
                    if (json.error) {
                        alert(json.error);
                    } else {
                        Cookies.set('userData', json, {expires: 1});
                        $('#myModal').modal('hide');
                        Account.getCookies();
                    }
                }
            });
        } else {
            alert('Please enter all fields');
        }
        return false;
    }
})
;

$(document).ajaxError(function (event, request, settings, exception) {
    var message;
    var statusErrorMap = {
        '400': "Server understood the request, but request content was invalid.",
        '401': "Unauthorized access.",
        '403': "Forbidden resource can't be accessed.",
        '500': "Internal server error.",
        '503': "Service unavailable.",
        '404': "Page not found"
    };
    if (request.status) {
        message = statusErrorMap[request.status];
        if (!message) {
            message = "Unknown Error \n.";
        }
        switch (request.status) {
            case 404:
                location.href = '/404.html';
                return false;
            case 401:
                Cookies.remove('userData');
                Account.userData = {};
                alert("Your session is expired.Please relogin.");
                location.href = '/index.html';
                return false;
            default:
                location.href = '/500.html?errorStatus=' + request.status + '&errorMsg=' + message;
                return false;
        }
    } else if (exception == 'parsererror') {
        message = "Error.\nParsing JSON Request failed.";
    } else if (exception == 'timeout') {
        message = "Request Time out.";
    } else if (exception == 'abort') {
        message = "Request was aborted by the server";
    } else {
        message = "Unknown Error \n.";
    }
    location.href = '/500.html?errorStatus=' + request.status + '&errorMsg=' + message;
});

