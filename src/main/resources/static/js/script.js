const baseUrl = "http://localhost:8082/cashflow";

const didUri = localStorage.getItem("didUri");
const downloadUrl =`${baseUrl}/users/${didUri}/download`;

$('.downloadKey').on('click', function() {
    console.log(downloadUrl)
    $.ajax({
        url: downloadUrl,
        type: 'GET',
        success: function(data) {
            console.log(data);
            downloadFile(data)
        }
    });
});

async function downloadFile(data) {
    console.log(data);

    const blob = new Blob([data], { type: 'text/plain' });

    // This part is to to check if the browser actually supports this file system access api
    if (window.showSaveFilePicker) {
        try {
            const handle = await window.showSaveFilePicker({
                suggestedName: 'key.txt',
                types: [
                    {
                        description: 'Text Files',
                        accept: { 'text/plain': ['.txt'] }
                    }
                ]
            });

            const writableStream = await handle.createWritable();

            await writableStream.write(blob);

            await writableStream.close();

            console.log('File saved successfully!');
            window.location.href = "/cashflow/dashboard"
        } catch (err) {
            console.error('Error saving file:', err);
            window.location.href = "/cashflow/dashboard"
        }
    } else {
        console.log('This API is not supported in this browser.');
    }
}


const loginContainer = document.getElementById('login-container');
const overlay = document.getElementById('overlay');
const openOverlay = document.getElementById('openOverlay');
const closeOverlay = document.getElementById('closeOverlay');

openOverlay.addEventListener('click', function(event) {
    event.preventDefault();
    overlay.style.display = 'flex';
    loginContainer.style.filter = 'blur(5px)';
});

closeOverlay.addEventListener('click', function() {
    overlay.style.display = 'none';
    loginContainer.style.filter = 'none';
});

window.addEventListener('click', function(event) {
    if (event.target === overlay) {
        overlay.style.display = 'none';
        loginContainer.style.filter = 'none';
    }
});