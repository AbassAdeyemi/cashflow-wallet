$(document).ready(function() {
    // $('#customAlert').removeClass('hidden');
    const didUri = localStorage.getItem('didUri');
    const balance = localStorage.getItem("balance");
    if(!balance){
        localStorage.setItem("balance", "0")
        const balance = parseFloat(localStorage.getItem("balance"))
        setBalance(balance)
    } else {
        $('.dollar-amount').text(balance)
    }
    if (didUri !== null) {
        const profileUrl = `${baseUrl}/users/${didUri}/profile`;
        makeProfileRequest(profileUrl);
        addOrderHistory(didUri)
    } else {
        // remember to add a text on the import card that says; user wasn't found, gotyour keys? <-upload->, if not register.
        if (window.location.pathname !== '../import.html') {
            window.location.href = '/cashflow/import';
        }
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
    const kccContainer = $('.kcc-container')

    if(profileData.credentials.length === 0) {
        kccContainer.append(`<p class="no-items">No credentials to display</p>`)
    }
    for (const credential of profileData.credentials) {
        const credentialHtml = ` <div class="userKcc">
                    <div class="issue">
                        <span for="">Issuer: </span>
                        <p class="issuer">${credential.issuer}</p>
                    </div>
                    <div class="cred-type">
                        <span for="">Type: </span>
                        <p class="credType">${credential.type}</p>
                    </div>
                    <div class="issue-date">
                        <span for="">Issuance Date: </span>
                        <p class="issuanceDate">${credential.issuanceDate}</p>
                    </div>
                    <div class="exp-date">
                        <span for="">Expiration Date: </span>
                        <p class="expiration">${credential.expirationDate}</p>
                    </div>
                </div>`
        kccContainer.append(credentialHtml)
    }
}

$('.tag').on('click', function() {
    $('.funding-detail').removeClass('hidden')
});

// ***Add to balance***
$('.fund-btn').on('click', function() {
    const balance = parseFloat(localStorage.getItem("balance"))
    const newBalance = balance + 1;
    setBalance(newBalance)
})

function setBalance(balance) {
    const dollarAmount = $('.dollar-amount')
    dollarAmount.text(balance);
    localStorage.setItem("balance", balance.toFixed(2))
}

// ***close***
$('#close-icon').on('click',function () {
    $('.funding-detail').addClass('hidden');
})

function addOrderHistory(customerDID) {
    $.ajax({
        url: `${baseUrl}/exchanges/quotes/${customerDID}/history`
    }).done(function (data) {
        appendHistory(data)
    }).fail(function (e){
        console.log("Could not get order history")
    })
}

function appendHistory(orders) {
    const orderContainer = $('.recent-transactions')
    const thead = $('.recent-transactions thead')
    const tbody = $('.recent-transactions tbody')
    if(orders.length === 0) {
        thead.addClass("hidden")
        orderContainer.append(`<p class="no-items">No transactions to display</p>`)
    }
    for(const order of orders) {
        const row = $('<tr></tr>');
        row.append('<td>' +order.pfiName+ '</td>')
        row.append('<td>' +(order.payin.amount + " "+order.payin.currencyCode)+ '</td>')
        row.append('<td>' +(order.payout.amount + " "+order.payout.currencyCode)+ '</td>')
        row.append('<td>' +order.completedAt+ '</td>')
        tbody.append(row)
    }
}

$('.log-out').on('click', function () {
   localStorage.clear()
   window.location.href = '/cashflow/import'
});

