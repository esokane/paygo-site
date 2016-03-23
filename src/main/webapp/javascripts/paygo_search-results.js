$(document).ready(function () {
	location.queryString = {};
	location.search.substr(1).split("&").forEach(function (pair) {
		if (pair === "") {
			return;
		}
		var parts = pair.split("=");
		location.queryString[parts[0]] = parts[1] && decodeURIComponent(parts[1].replace(/\+/g, " "));
	});
	$.ajax({
		url: '/reportServlet/searchResults',
		method: 'POST',
		contentType: 'application/json',
		dataType: 'json',
		processData: false,
		data: JSON.stringify(location.queryString),
		complete: function (response) {
			var json = response.responseJSON;
			if (json.error) {
				$('#alertNotFound').show();
				$('.selectReport-wrapper').hide();
				return;
			}
			if (json.length > 0) {
				json[0].selected = true;
			}
			var tmpl = $.templates('#ALL');
			var html = tmpl.render({response: json});
			$('.companySearchSummary').html('1-' + json.length + ' results for "' + location.queryString.company + ', ' + location.queryString.city + ' ' + location.queryString.state + '"');
			$('.resultsDisplayWrapper').html(html);
			var resultsDisplayWrapperRadio = $('.resultsDisplayWrapper input:radio');
			resultsDisplayWrapperRadio.change(function () {
				$('div.radio-selected').removeClass('radio-selected');
				$(this).closest('.companySearchResult').addClass('radio-selected');
				$('.selected').empty();
				$('div.radio-selected span.companyName').clone().appendTo(".selected");
				$(".selected").append(" ");
				$('div.radio-selected span.companyAddress').clone().appendTo(".selected");
			});
			resultsDisplayWrapperRadio[0].click();
		}
	});
	// login form switch
	$("input[name='switch-reg']").on("change", function changeLoginRegistrationForm() {
		var loginButton = $("#show-login");
		var registerButton = $("#show-registration");
		var loginForm = $("#login-form-container");
		var registerForm = $("#registration-form-container-order");
		if (loginButton.is(':checked') && !loginForm.is(':visible')) {
			loginForm.show();
			registerForm.hide();
			registerButton.attr('checked', false);
		} else if (registerButton.is(':checked') && !registerForm.is(':visible')) {
			registerForm.show();
			loginForm.hide();
			loginButton.attr('checked', false);
		}
	});
	var add2CartServlet = function (orderData) {
		Account.orderData.userId = Account.userData.userId;
		$.ajax({
			url: '/reportServlet/add2Cart',
			method: 'POST',
			contentType: 'application/json',
			dataType: 'json',
			processData: false,
			data: JSON.stringify(orderData),
			complete: function (response) {
				var json = response.responseJSON;
				if (json.error) {
					alert(json.error);
					return true;
				}
				location.href = '/reviewOrder.html';
			}
		});
	};
	// button "Log In" event OnClick
	$('#login-submit-order').click(function () {
		var email = $('#login-email-order').val(),
			password = $('#login-password-order').val();
		// if email and password exist
		if (email.match(/^[\w\-\.]+@[\w\-\.]+\.\w+$/) && password) {
			// authorize user
			Account.logIn({
				email: email,
				password: password,
				complete: function (response) {
					var json = response.responseJSON;
					if (json.error) {
						alert('LogIn Fail');
					} else {
						Cookies.set('userData', json, {expires: 1});
						Account.getCookies();
						if (Account.orderData.companyId != 0) {
							add2CartServlet(Account.orderData);
						}
					}
				}
			});
		} else {
			alert('Please enter all fields');
		}
		return false;
	});



	// button "Register" event onClick
	$('#register-submit-order').click(function () {
		var email = $('#login-email-order').val(),
			firstName = $('#first-name-order').val(),
			lastName = $('#last-name-order').val(),
			password = $('#register-password-order').val(),
			retypePassword = $('#retype-password-order').val();
		// form validation
		if (email.match(/^[\w\-\.]+@[\w\-\.]+\.\w+$/) && firstName && lastName && password && password === retypePassword) {
			// user signig up
			Account.signUp({
				email: email,
				firstName: firstName,
				lastName: lastName,
				password: password,
				complete: function (response) {
					var json = response.responseJSON;
					if (json.error) {
						alert('SignUp Fail');
					} else {
						Cookies.set('userData', json, {expires: 30});
						Account.getCookies();
						if (Account.orderData.companyId != 0) {
							add2CartServlet(Account.orderData);
						}
					}
				}
			});
		} else {
			alert('Please enter all fields');
		}
		return false;
	});



	$('button.prodSubmit').click(function (button) {
		Account.orderData = {
			company:companyId = {companyId: $('.resultsDisplayWrapper .radio-selected input:radio').val()},
			reportType:id = {reportId: button.currentTarget.attributes.getNamedItem('data-price').value}
		};
		Account.orderData.reportType = {
			id: button.currentTarget.attributes.getNamedItem('data-price').value
		};
		if (Account.userData) {
			add2CartServlet(Account.orderData);
		} else {
			$('#loginBlock').modal('show');
		}
	});
});

