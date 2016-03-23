$(document).ready(function () {
	$('#btn-search').click(function () {
		var company = $('#company').val(),
			city = $('#city').val(),
			state = $('#state').val();
		if (!company || !state) {
			alert('Please enter both Company name and State/Province.');
			return false;
		}
		location.href = '/search-results.html?company=' + company + '&city=' + city + '&state=' + state;
	})
});
