const baseUrl = "http://localhost:8082";

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
    var url = `http://localhost:8082/offerings/match?fromCurrency=${fromParam}&toCurrency=${toParam}`

    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            matchingOffer(data);

        }
    })

}

function matchingOffer(items) {
    var matchBody = $('#match tbody');
    matchBody.empty();

    for (let i = 0; i < items.length; i++) {
        const offering = items[i];


        var row = $('<tr></tr>');

        row.append('<td>' + offering.pfiName + '</td>');
        row.append('<td>' + offering.rate + '</td>');

        // console.log(offering.payInMethods);

        var button = $(`<button class="match-btn" data-rate='${(offering.rate)}' data-payin='${JSON.stringify(offering.payInMethods)}' data-payout='${JSON.stringify(offering.payOutMethods)}'>Trade</button>`);
        var buttonCell = $('<td></td>');
        buttonCell.append(button);
        row.append(buttonCell);




        matchBody.append(row);
        localStorage.setItem('offeringRef', offering.ref);


    }
    $('.match-btn').on('click', function() {
        const didUri = localStorage.getItem("didUri");
        const offeringRef = localStorage.getItem("offeringRef");

        var payInMethods = JSON.parse($(this).attr('data-payin'));
        var payOutMethods = JSON.parse($(this).attr('data-payout'));
        var rate = $(this).data('rate');

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

        // console.log('success');

        const did = localStorage.getItem("didUri");


        var formData = {
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
    if(paymentMethodSatisfiesCheck(payInMethods)) {
        localStorage.setItem("payInMethod", JSON.stringify(payInMethods[0]))
    }
    if(paymentMethodSatisfiesCheck(payOutMethods)) {
        localStorage.setItem("payOutMethod", JSON.stringify(payOutMethods[0]))
    }
    localStorage.setItem("rate", rate)
}

const paymentMethodSatisfiesCheck = (paymentMethods) => {
    return paymentMethods.length > 0 && paymentMethods[0].paymentFields.length > 0
}
