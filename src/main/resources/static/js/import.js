const baseUrl = "http://localhost:8082";

$(document).ready(function() {
    $('.generate').on('click', function() {
        $.ajax({
            url: `${baseUrl}/users/register`,
            method: 'POST',
            contentType: 'application/json',
            success: function(data) {
                window.location.href = '../dashboard.html'
                localStorage.setItem('didUri', (data.didUri))
            },
        });
    });
});

