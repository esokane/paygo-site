$(document).ready(function () {
    var parts= [];
    location.search.substr(1).split("&").forEach(function (pair) {
        if (pair === "") {
            return;
        }
        parts.push(pair.split("="));
    });
    $('#error-status').html(parts[0][1].replace(/%20/g, " "));
    $('#error-msg').html(parts[1][1].replace(/%20/g, " "));
    /**signing with Google  */
    var startApp = function () {
        if (gapi) {
            gapi.load('auth2', function () {
                    // Retrieve the singleton for the GoogleAuth library and set up the client.
                    auth2 = gapi.auth2.init({
                        client_id: '1098810344021-180ph8hjq149pcb7l690o3pt9d2fg022.apps.googleusercontent.com',
                        cookiepolicy: 'single_host_origin',
                        // Request scopes in addition to 'profile' and 'email'
                        //scope: 'additional_scope'
                    });
                    attachSignin(document.getElementById('btn-google'), $('#myModal'));
                    attachSignin(document.getElementById('btn-google-sign'), $('#signUp'));
                }
            );
        }
    };

    function attachSignin(element, form) {
        console.log(element.id);
        auth2.attachClickHandler(element, {},
            function (googleUser) {
                var id_token = googleUser.getAuthResponse().id_token;
                Account.googleSignIn({
                    id_token: id_token,
                    complete: function (response) {
                        var json = response.responseJSON;
                        if (json.error) {
                            alert(json.error);
                        } else {
                            Cookies.set('userData', json, {expires: 30});
                            form.modal('hide');
                            Account.getCookies();
                        }
                    }
                });

            }, function (error) {
                alert(JSON.stringify(error, undefined, 2));
            });
    }

    startApp();
});



