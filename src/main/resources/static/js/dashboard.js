$(document).ready(function() {
    const didUri = localStorage.getItem('didUri');
    if (didUri !== null) {
        const url = `${baseUrl}/users/${didUri}/profile`;
        makeProfileRequest(url);
    } else {
        // remember to add a text on the import card that says; user wasn't found, gotyour keys? <-upload->, if not register.
        if (window.location.pathname !== '../import.html') {
            window.location.href = '../import';
        }
        console.log("User ID not found in localStorage");
    }


});


function makeProfileRequest(url) {
    $.ajax({
        url: url,
        type: "GET",
        success: function(data) {
            generateUserProfile(data);
        },
    });
}

function generateUserProfile(profileData) {
    for (const credential of profileData.credentials) {
        $('.issuer').html(credential.issuer);
        $('.issuanceDate').html(credential.issuanceDate);
        $('.expiration').html(credential.expirationDate);
        $('.credType').html(credential.type)
    }
}
