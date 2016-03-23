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

});



