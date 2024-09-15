$(document).ready(function() {
    handleDIDUpload();
    handleKeyGeneration();
    const didUri = localStorage.getItem("didUri")
    if(didUri) {
        window.location.href='/cashflow/dashboard'
    }
});

function handleDIDUpload() {
    $('#upload-form').on('submit', function(e) {
        e.preventDefault();

        const formData = new FormData();
        const fileInput = $('#file-input')[0].files[0];

        if (!fileInput) {
            alert("Please select a file.");
            return;
        }

        formData.append('file', fileInput);

        $.ajax({
            url: baseUrl + '/users/upload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                console.log(response.didUri);
                const didUri = response.didUri
                localStorage.setItem("didUri", didUri);
                window.location.href = '/cashflow/dashboard'

            },
            error: function(e) {
                $('.error-html p').text(e.responseJSON.detail)
                $('.error-html').removeClass('hidden')
            }
        });
    });
}

function handleKeyGeneration() {
    $('.generate').on('click', function() {
        $('.roller').show();
        $.ajax({
            url: `${baseUrl}/users/register`,
            method: 'POST',
            contentType: 'application/json',
            success: function(data) {
                window.location.href = '/cashflow/dashboard'
                localStorage.setItem('didUri', (data.didUri))
            },
        });
    });
}

$('#error-cancel').on('click', () => {
    $('.error-html').addClass('hidden')
})