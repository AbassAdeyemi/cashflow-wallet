$(document).ready(function () {
    const fromCurrency = localStorage.getItem("from");
    const toCurrency = localStorage.getItem("to");

    if (fromCurrency && toCurrency) {
        getAvailablePairs(fromCurrency, toCurrency)
    }
})

//   Matching offerings


function getAvailablePairs(fromCurrency, toCurrency) {
    const fromParam = fromCurrency;
    const toParam = toCurrency;
    const url = `http://localhost:8082/offerings/match?fromCurrency=${fromParam}&toCurrency=${toParam}`

    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            matchingOffer(data);

        }
    })

}

function matchingOffer(items) {
    const matchBody = $('#match tbody');
    matchBody.empty();

    for (let i = 0; i < items.length; i++) {
        const offering = items[i];


        const row = $('<tr></tr>');

        row.append('<td>' + offering.pfiName + '</td>');
        row.append('<td>' + offering.rate + '</td>');
        row.append('<td>' + offering.payInMethods[0].kind + '</td>')
        const ratingStars = $('<td></td>').html(generateStars(offering.pfiRating));
        row.append(ratingStars);

        // console.log(offering.payInMethods);

        const button = $(`<button class="match-btn" data-rate='${(offering.rate)}' data-payin='${JSON.stringify(offering.payInMethods)}' data-payout='${JSON.stringify(offering.payOutMethods)}'>Trade</button>`);
        const buttonCell = $('<td></td>');
        buttonCell.append(button);
        row.append(buttonCell);




        matchBody.append(row);
        localStorage.setItem('offeringRef', offering.ref);


    }
    $('.match-btn').on('click', function() {
        const didUri = localStorage.getItem("didUri");
        const offeringRef = localStorage.getItem("offeringRef");

        const payInMethods = JSON.parse($(this).attr('data-payin'));
        const payOutMethods = JSON.parse($(this).attr('data-payout'));
        const rate = $(this).data('rate');

        $.ajax({
            url: `http://localhost:8082/offerings/has-credential?didUri=${didUri}&offeringRef=${offeringRef}`,
            type: 'GET',
            success: function(response) {

                const resultContainer = $("#resultContainer");
                if (response === true) {
                    passPaymentMethod(payInMethods, payOutMethods, rate)
                    window.location.href = "../payment.html";
                } else {
                    resultContainer.removeClass('hidden');
                    obtainKcc(payInMethods, payOutMethods, rate)
                }

            },
            error: function () {
                console.error('not successful');
            }

        })
    })
}
// ***Obtain Kcc***

function obtainKcc(payInMethods, payOutMethods, rate) {

    $('.form-submit').on('submit', function(e) {

        e.preventDefault();

        const did = localStorage.getItem("didUri");


        const formData = {
            customerName: $('.name-input').val(),
            countryCode: $('.code-input').val(),
            customerDID: did
        }

        $.ajax({
            url: 'http://localhost:8082/users/credentials',
            type: 'POST',
            data: JSON.stringify(formData),
            contentType: 'application/json',
            success: function () {
                passPaymentMethod(payInMethods, payOutMethods, rate)
                window.location.href = "../payment.html"
            },
            error: function () {
                console.error("not successful");

            }
        })

    })
}

function passPaymentMethod(payInMethods, payOutMethods, rate) {
    if(paymentMethodSatisfiesCheck(payInMethods[0])) {
        localStorage.setItem("payInMethod", JSON.stringify(payInMethods[0]))
    } else {
        localStorage.setItem("payInMethodKind", payInMethods[0].kind)
    }
    if(paymentMethodSatisfiesCheck(payOutMethods)) {
        localStorage.setItem("payOutMethod", JSON.stringify(payOutMethods[0]))
    } else {
        localStorage.setItem("payOutMethodKind", payOutMethods[0].kind)
    }
    localStorage.setItem("rate", rate)
}

const paymentMethodSatisfiesCheck = (paymentMethods) => {
    return paymentMethods.length > 0 && paymentMethods[0].paymentFields.length > 0
}

function generateStars(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = (rating % 1) >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;

    let starsHtml = '';

    for (let i = 0; i < fullStars; i++) {
        starsHtml += '<span class="star">&#9733;</span>';

    }

    if (halfStar) {
        starsHtml += '<span class="star">&#9733;</span>';
    }

    for (let j = 0; j < emptyStars; j++) {
        starsHtml += '<span class="star">&#9734;</span>';
    }

    return starsHtml;
}
