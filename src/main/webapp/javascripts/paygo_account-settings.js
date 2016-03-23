$(document).ready(function () {
    if (Account.userData) {
        $('#email').val(Account.userData.email);
        $('#firstName').val(Account.userData.firstName);
        $('#lastName').val(Account.userData.lastName);
        if (Account.userData.address) {
            $('#street1').val(Account.userData.address.street1);
            $('#street2').val(Account.userData.address.street2);
            $('#city').val(Account.userData.address.city);
            $('#state').val(Account.userData.address.state);
            $('#zip').val(Account.userData.address.zip);
            $('#country').val(Account.userData.address.country);
            $('#phone').val(Account.userData.address.phone);
        }
        if (Account.userData.card) {
        $('#firstNameCard').val(Account.userData.card.firstName);
        $('#lastNameCard').val(Account.userData.card.lastName);
        $('#cardNumber').val(Account.userData.card.cardNumber);
        $('#expireMM').val(Account.userData.card.expireMM);
        $('#expireYY').val(Account.userData.card.expireYY);
    }
    }
       /*deleting user*/
    $('#btnYes').click(function (button) {
        $.ajax({
            url: '/reportServlet/deleteUser',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            complete: function (response) {
                var json = response.responseJSON;
                if (json.error) {
                    alert(json.error);
                    return false;
                }
                Cookies.remove('userData');
                Account.userData = {};
                location.href = '/index.html';

            }
        });
    });
    /*saving updated info*/
    $('#btnSave').click(function () {
        var formData = {
            firstName: $('#firstName').val(),
            lastName: $('#lastName').val(),
            email: $('#email').val(),
            password: $('#pass').val(),
            newPass: $('#newPass').val(),
            newPassConfirm: $('#newPassConfirm').val(),
            address: {
                street1: $('#street1').val(),
                street2: $('#street2').val(),
                city: $('#city').val(),
                state: $('#state').val(),
                zip: $('#zip').val(),
                country: $('#country').val(),
                phone: $('#phone').val()
            },
            card: {
                firstName: $('#firstNameCard').val(),
                lastName: $('#lastNameCard').val(),
                cardNumber: $('#cardNumber').val(),
                expireMM: $('#expireMM').val(),
                expireYY: $('#expireYY').val(),
            }
        };
        if ((formData.newPass != "") || (formData.newPassConfirm != "")) {
            if (formData.newPass != formData.newPassConfirm){
                alert("Password and password confirmation are not same!");
                $('#pass').val("");
                $('#newPass').val("");
                $('#newPassConfirm').val("");
                return false;
            }
            if (!((formData.newPass != "") && (formData.newPassConfirm != "") && (formData.pass != ""))) {
                alert("Please enter all current password, new password, password confirmation to change the password!");
                $('#pass').val("");
                $('#newPass').val("");
                $('#newPassConfirm').val("");
                return false;
            }
        }
            $.ajax({
                url: '/reportServlet/saveUser',
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                processData: false,
                data: JSON.stringify(formData),
                complete: function (response) {
                    var json = response.responseJSON;
                    if (json.error) {
                        alert(json.error);
                        return false;
                    }
                    else{
                        Account.userData = json;
                        Cookies.set('userData', json, {expires: 1});
                        alert("Account was successfully saved");
                        location.href = '/index.html';
                    }
                }
            });
    });
});



