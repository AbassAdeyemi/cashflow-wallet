document.addEventListener('DOMContentLoaded', () => {
    const links = document.querySelectorAll('a');
    const pages = document.querySelectorAll('.content .main-body');

    function showPage(pageId) {
        pages.forEach(page => {
            if (page.id === pageId) {
                page.classList.add('show');
            } else {
                page.classList.remove('show');
            }
        });
    }

    links.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const pageId = link.getAttribute('data-page');
            showPage(pageId);
        });
    });

    showPage('dashboard');
});

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