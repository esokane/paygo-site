$(document).ready(function () {
        $.ajax({
            url: '/reportServlet/getRecentReports',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            complete: function (response) {
                var json = response.responseJSON;
                if (json.error) {
                    alert(json.error);
                    return true;
                }
                else {
                    var tmpl = $.templates('#PDFs');
                    var html = tmpl.render({response: json});
                    $('#pdfRow').html(html);
                }
            },
        });
    /* reload user info (it could be changed on a previous page)*/
    Account.getUserInfo({
        complete: function (response) {
            var json = response;
            Account.userData = json;
            Cookies.set('userData', json, {expires: 1});
        }
    });
    $("sendToUserEmail").html(Account.userData.email);
});
