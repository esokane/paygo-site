$(document).ready(function () {
        $.ajax({
            url: '/reportServlet/getReports',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            complete: function (response) {
                var json = response.responseJSON;
                var tmpl = $.templates('#PDFs');
                var html = tmpl.render({response: json});
                $('#pdfRow').html(html);
            },
        });
    $("sendToUserEmail").html(Account.userData.email);
});
