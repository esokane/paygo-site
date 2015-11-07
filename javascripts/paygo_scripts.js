$(document).ready(function () {

	$('[data-toggle="tooltip"]').tooltip();
	
	
	/*** login form switch ***/
	
	$("input[name='switch-reg']").on("change", function changeLoginRegistrationForm() {
		var loginButton = $("#show-login");
		var registerButton = $("#show-registration");
		var loginForm = $("#login-form-container");
		var registerForm = $("#registration-form-container");
		if (loginButton.is(':checked') && !loginForm.is(':visible')) {
			loginForm.show();
			registerForm.hide();
			registerButton.attr('checked', false);
		}
		else if (registerButton.is(':checked') && !registerForm.is(':visible')) {
			registerForm.show();
			loginForm.hide();
			loginButton.attr('checked', false);
		}
	});
	
	//change color when radio is selected
	$('.resultsDisplayWrapper input:radio').change(function () {
		// Only remove the class in the specific `box` that contains the radio
		$('div.radio-selected').removeClass('radio-selected');
        $(this).closest('.companySearchResult').addClass('radio-selected');
		$('.selected').empty();
		$('div.radio-selected span.companyName').clone().appendTo( ".selected" );
		$(".selected").append(" ");
		$('div.radio-selected span.companyAddress').clone().appendTo( ".selected" );
	});
	

	$('.empty-cart').hide();
	
	$('#delete-modal').on('show', function() {
		var id = $(this).closest('tr').data('id'),
			removeBtn = $(this).find('.danger');
	});
	$('.confirm-delete').on('click', function(e) {
		e.preventDefault();
		var id = $(this).closest('tr').data('id');
		$('#delete-modal').data('id', id).modal('show');
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
	
	
});