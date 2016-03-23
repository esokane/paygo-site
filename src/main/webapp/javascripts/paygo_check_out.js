$(document).ready(function () {
    Account.getUserInfo({
        complete: function (response) {
            var json = response;
            Account.userData = json;
            Cookies.set('userData', json, {expires: 1});
            $('#email').val(Account.userData.email);
            if (Account.userData.address) {
                $('#street1').val(Account.userData.address.street1);
                $('#street2').val(Account.userData.address.street2);
                $('#state').val(Account.userData.address.state);
                $('#city').val(Account.userData.address.city);
                $('#zip').val(Account.userData.address.zip);
                $('#country').val(Account.userData.address.country);
            }
            if (Account.userData.card) {
                $('#firstName').val(Account.userData.card.firstName);
                $('#lastName').val(Account.userData.card.lastName);
                $('#cardNumber').val(Account.userData.card.cardNumber);
                $('#expireMM').val(Account.userData.card.expireMM);
                $('#expireYY').val(Account.userData.card.expireYY);
            }
        }
    });


    $('#submit-payment').click(function () {
        var phone2 = "";
        if (Account.userData.address)
        {
            phone2 = Account.userData.address.phone
        }
        var formData = {
            address: {
                street1: $('#street1').val(),
                street2: $('#street2').val(),
                city: $('#city').val(),
                state: $('#state').val(),
                zip: $('#zip').val(),
                country: $('#country').val(),
                phone: phone2
            },
            card: {
                firstName: $('#firstName').val(),
                lastName: $('#lastName').val(),
                cardNumber: $('#cardNumber').val(),
                expireMM: $('#expireMM').val(),
                expireYY: $('#expireYY').val(),
                cvv: $('#cvv').val()
            }
        };
        if (
            formData.address.street1
            && formData.address.city
            && formData.address.state
            && formData.address.zip
            && formData.address.country
            && formData.card.firstName
            && formData.card.lastName
            && formData.card.cardNumber
            && formData.card.expireMM
            && formData.card.expireYY
            && formData.card.cvv
        ) {
            $.ajax({
                url: '/reportServlet/checkout',
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                processData: false,
                data: JSON.stringify(formData),
                success: function (response) {
                    var json = response;
                    if (json.error) {
                        alert(json.error);
                        return false;
                    }
                    if (!json.success) {
                        if (json.length > 0) {
                            json[0].selected = true;
                        }
                        var tickerList = [];
                        var i = 0;
                        var showSelectTicker = function (json) {
                            var tmpl = $.templates('#ALL');
                            json[i].i = i.toString();
                            var html = tmpl.render({response: json[i]});
                            $('.resultsDisplayWrapper').html(html);
                            var resultsDisplayWrapperRadio = $('.resultsDisplayWrapper input:radio');
                            resultsDisplayWrapperRadio.change(function () {
                                $('div.radio-selected').removeClass('radio-selected');
                                $(this).closest('.companyTicker').addClass('radio-selected');
                                $('.selected').empty();
                                $('div.radio-selected span.companyName').clone().appendTo(".selected");
                                $(".selected").append(" ");
                                $('div.radio-selected span.companyAddress').clone().appendTo(".selected");
                                return false;
                            });
                            $('#apply-btn'.concat(json[i].i)).click(function () {
                                ticker = $('.resultsDisplayWrapper .radio-selected input:radio').val();
                                tickerList.push({  requestId: json[i].requestId,
                                    tickers: [{ticker:ticker}]
                                });
                                $('#myModal'.concat(json[i].i)).modal('hide');
                                i++;
                                if (i < json.length)
                                    showSelectTicker(json);
                                else{
                                    selectTicker(tickerList);
                                    return false;
                                }
                            });
                            $('#skip-btn'.concat(json[i].i)).click(function () {
                                tickerList.push({
                                    tickers: [{ticker: null}],
                                    requestId: json[i].requestId
                                });
                                $('#myModal'.concat(json[i].i)).modal('hide');
                                i++;
                                if (i < json.length)
                                    showSelectTicker(json);
                                else{
                                    selectTicker(tickerList);
                                    return false;
                                }
                            });
                            resultsDisplayWrapperRadio[0].click();
                            $('#myModal'.concat(json[i].i)).modal('show');
                        };
                        if (i < json.length)
                          showSelectTicker(json);
                        else
                          selectTicker(tickerList);
                    }
                    else {
                        location.href = '/order-complete.html';
                    }
                }
            });
        } else {
            alert('Please enter all fields');
        }
        return false;
    });

    var selectTicker = function (tickerList) {
        $.ajax({
            url: '/reportServlet/selectTicker',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            data: JSON.stringify(tickerList),
            complete: function (response) {
                var json = response.responseJSON;
                if (json.error) {
                    alert(json.error);
                    return false;
                }
                location.href = '/order-complete.html';
                return false;
            }
    });
    };

    if (Account.userData) {
        $.ajax({
            url: '/reportServlet/viewCart',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            data: JSON.stringify({
                userId: Account.userData.userId
            }),
            success: function (response) {
                var json = response;
                $('#subtotal-items').html(json.length);
                var total = 0;
                json.forEach(function (v) {
                    total += parseFloat(v.reportType.price);
                });
                $('#subtotal-cost').html(total);
                $('#total-cost').html(total);
            }
        });
    }
});
