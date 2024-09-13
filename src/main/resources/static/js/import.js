$(document).ready(function() {
    handleDIDUpload();
    handleKeyGeneration();
    const didUri = localStorage.getItem("didUri")
    if(didUri) {
        window.location.href='../dashboard'
    }
});

function handleDIDUpload() {
    $('#upload-form').on('submit', function(e) {
        e.preventDefault();

        var formData = new FormData();
        var fileInput = $('#file-input')[0].files[0];

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
                window.location.href = '../dashboard'

            },
            error: function() {
                // $('#upload-status').html('<p>Failed to upload file: ' + error + '</p>');
                // console.error('no file chosen');

            }
        });
    });
}

function handleKeyGeneration() {
    $('.generate').on('click', function() {
        $.ajax({
            url: `${baseUrl}/users/register`,
            method: 'POST',
            contentType: 'application/json',
            success: function(data) {
                window.location.href = '../dashboard'
                localStorage.setItem('didUri', (data.didUri))
            },
        });
    });
}