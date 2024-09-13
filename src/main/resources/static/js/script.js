const baseUrl = "http://localhost:8082";

// document.addEventListener('DOMContentLoaded', () => {
//     const links = document.querySelectorAll('a');
//     const pages = document.querySelectorAll('.content .main-body');
//
//     function showPage(pageId) {
//         pages.forEach(page => {
//             if (page.id === pageId) {
//                 page.classList.add('show');
//             } else {
//                 page.classList.remove('show');
//             }
//         });
//     }
//
//     links.forEach(link => {
//         link.addEventListener('click', (e) => {
//             e.preventDefault();
//             const pageId = link.getAttribute('data-page');
//             showPage(pageId);
//         });
//     });
//
//     showPage('dashboard');
// });


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

    // This part is to to check if the browser actually supports this file system access Api
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
            window.location.href = "../dashboard.html"
        } catch (err) {
            console.error('Error saving file:', err);
            window.location.href = "../dashboard.html"
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