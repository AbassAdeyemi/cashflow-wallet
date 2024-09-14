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
    const url = `${baseUrl}/offerings/match?fromCurrency=${fromParam}&toCurrency=${toParam}`

    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            console.log(data)
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

        const button = $(`<button class="match-btn" data-rate='${(offering.rate)}' 
                    data-payin='${JSON.stringify(offering.payInMethods)}' 
                    data-payout='${JSON.stringify(offering.payOutMethods)}'
                    data-payout='${JSON.stringify(offering.payOutMethods)}'
                    data-ref="${offering.ref}"
                    data-pfiDID="${offering.pfiDID}"
                    >Trade</button>`);
        const buttonCell = $('<td></td>');
        buttonCell.append(button);
        row.append(buttonCell);




        matchBody.append(row);
    }
    $('.match-btn').on('click', function() {
        const didUri = localStorage.getItem("didUri");
        const offeringRef = $(this).data('ref')
        const pfiDID = $(this).data('pfidid')
        const rate = $(this).data('rate');

        localStorage.setItem("offeringRef", offeringRef);
        localStorage.setItem("pfiDID", pfiDID);
        localStorage.setItem("rate", rate);

        const payInMethods = JSON.parse($(this).attr('data-payin'));
        const payOutMethods = JSON.parse($(this).attr('data-payout'));


        $.ajax({
            url: `${baseUrl}/offerings/has-credential?didUri=${didUri}&offeringRef=${offeringRef}`,
            type: 'GET',
            success: function(response) {

                const resultContainer = $("#resultContainer");
                if (response === true) {
                    passPaymentMethod(payInMethods, payOutMethods)
                    window.location.href = "/cashflow/payment";
                } else {
                    resultContainer.removeClass('hidden');
                    obtainKcc(payInMethods, payOutMethods)
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

        const errorHtml = $('.error-html')
        $('.error-html p').empty()
       const validationErrors = getValidationErrors(formData)
        if(validationErrors.length !== 0) {
            validationErrors.forEach(err => {
                errorHtml.append(`<p>${err}</p>`)
            })
            errorHtml.removeClass('hidden')
        } else {
            $.ajax({
                url: baseUrl + '/users/credentials',
                type: 'POST',
                data: JSON.stringify(formData),
                contentType: 'application/json',
                success: function () {
                    passPaymentMethod(payInMethods, payOutMethods, rate)
                    window.location.href = "/cashflow/payment"
                },
                error: function () {
                    console.error("not successful");

                }
            })
        }
    })
}

function passPaymentMethod(payInMethods, payOutMethods) {
    if(paymentMethodSatisfiesCheck(payInMethods)) {
        localStorage.setItem("payInMethod", JSON.stringify(payInMethods[0]))
    } else {
        localStorage.setItem("payInMethod", payInMethods[0].kind)
    }
    if(paymentMethodSatisfiesCheck(payOutMethods)) {
        localStorage.setItem("payOutMethod", JSON.stringify(payOutMethods[0]))
    } else {
        localStorage.setItem("payOutMethod", payOutMethods[0].kind)
    }
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
        // starsHtml += '<td class="star">&#9733;</td>';
        starsHtml += '<span class="star"><i class="fas fa-star"></i></span>';

    }

    if (halfStar) {
        starsHtml += '<span class="star"><i class="fas fa-star-half-alt" id=""></i></span>';
    }

    for (let j = 0; j < emptyStars; j++) {
        starsHtml += '<span class="star"><i class="fa-regular fa-star"/></i></span>';
    }

    return starsHtml;
}

function getValidationErrors(formData) {
    const errorMessages = []
    if(formData.customerName.trim() === "") {
        errorMessages.push("customerName is required")
    }
    if(formData.countryCode.trim() === "") {
        errorMessages.push("countryCode is required")
    }
    return errorMessages
}

$('#error-cancel').on('click', () => {
    $('.error-html').addClass('hidden')
})
