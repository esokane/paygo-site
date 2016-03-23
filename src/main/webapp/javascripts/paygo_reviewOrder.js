$(document).ready(function () {
	if (Account.userData) {
		$('#welcomeUserName').html(Account.userData.userName);
	}
	var viewCart = function(userData){
		$.ajax({
			url: '/reportServlet/viewCart',
			method: 'POST',
			contentType: 'application/json',
			dataType: 'json',
			processData: false,
			data: JSON.stringify({
				userId: userData.userId
			}),
			complete: function (response) {
				var json = response.responseJSON;
				var tmpl = $.templates('#Orders');
				var html = tmpl.render({response: json});
				$('#checkout-table tbody').html(html);
				$('#subtotal-items').html(json.length);
				var total = 0;
				json.forEach(function (v) {
					total += parseFloat(v.reportType.price);
				});
				$('#subtotal-cost').html(total);
				$('#total-cost').html(total);
				$('.confirm-delete').on('click', function (e) {
					e.preventDefault();
					var id = $(this).closest('tr').data('id');
					$('#delete-modal').data('id', id).modal('show');
				});
			},
		});
	}

	if (Account.userData) {
		viewCart(Account.userData);
	}

	var deleteReportFromCart = function (cartEntryId) {
		$.ajax({
			url: '/reportServlet/deleteFromCart',
			method: 'POST',
			contentType: 'application/json',
			dataType: 'json',
			processData: false,
			data: JSON.stringify(cartEntryId),
			complete: function (response) {
				var json = response.responseJSON;
				if (json.error) {
					alert(json.error);
					return true;
				}
				else{
					$('#delete-modal').modal('hide');
					viewCart(Account.userData);
				}
			}
		});
	};

	$('#btn-continue').click(function () {
		if ($('#subtotal-items').html() > 0) {
			location.href = "check_out.html"
		}
	});

	$('#btnYes').click(function () {
		cartEntryId = {cartEntryId: $('#delete-modal').data('id')}
		deleteReportFromCart(cartEntryId);
	});
});
