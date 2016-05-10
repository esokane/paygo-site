$(document).ready(function () {

    <!-- handling response from Payeezy server -->
    var responseHandler = function (status, response) {
        $('#someHiddenDiv').hide();
        if (status != 201) {
            if (response.error && status != 400) {
                var error = response["error"];
                var errormsg = error["messages"];
                var errorcode = JSON.stringify(errormsg[0].code, null, 4);
                var errorMessages = JSON.stringify(errormsg[0].description, null, 4);
                alert('Error Code:' + errorcode + ', Error Messages:'
                    + errorMessages);
            }
            if (status == 400 || status == 500) {
                var errormsg = response.Error.messages;
                var errorMessages = "";
                for (var i in errormsg) {
                    var ecode = errormsg[i].code;
                    var eMessage = errormsg[i].description;
                    errorMessages = errorMessages + 'Error Code:' + ecode + ', Error Messages:'
                        + eMessage;
                }
                alert(errorMessages);
            }

            $('#submit-payment').prop('disabled', false);
        } else {
            var result = response.token.value;
            alert(result);
            $('#submit-payment').prop('disabled', false);
        }

    };

    <!-- Building JSON resquest and submitting request to Payeezy sever -->
    $('#submit-payment').click(function () {
        $(this).prop('disabled', true);
        var apiKey = "g4xEZj7j8whRhWourlu3jSBIHXGSJAok";
        var js_security_key = "js-2392df76d69da1312f57d629ba01b9262392df76d69da131";
        var auth = "true";
        var ta_token = "NOIW";
        <!-- Setting Key parameters for Payeezy gettoken method -->
        Payeezy.setApiKey(apiKey);
        Payeezy.setJs_Security_Key(js_security_key);
        Payeezy.setTa_token(ta_token);
        Payeezy.setAuth(auth);
        Payeezy.setcurrency("USD");
        Payeezy.createToken(responseHandler);
        $('#someHiddenDiv').show();
        return false;
    });
});
