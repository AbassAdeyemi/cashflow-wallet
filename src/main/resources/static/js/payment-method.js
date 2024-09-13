$(document).ready(function() {
    const payInMethod = JSON.parse(localStorage.getItem('payInMethod'));
    const payOutMethod = JSON.parse(localStorage.getItem('payOutMethod'));
    const rate = localStorage.getItem("rate");
    const payInMethodKind = localStorage.getItem("payInMethodKind")
    const payOutMethodKind = localStorage.getItem("payOutMethodKind")
    const payInCurrency = localStorage.getItem("from");
    const payOutCurrency = localStorage.getItem("to");

    paymentMethodBody(payInMethod,
        payOutMethod, rate,
        payInCurrency, payOutCurrency,
        payInMethodKind, payOutMethodKind
    );
})

function paymentMethodBody(payInMethod, payOutMethod,
                           rate, payInCurrency,
                           payOutCurrency, payInMethodKind,
                           payOutMethodKind) {
    var payInContainer = $(".bank-info-container .bank-container");
    var payOutContainer = $(".bank-info-container-2 .bank-container-2");
    var conversionFields = $(".transaction-detail-container .transaction-detail")

    let payInHtmls = []
    let payOutHtmls = []
    if (payInMethod !== null) {
        const {div, htmls} = attachPaymentFields(payInMethod)
        payInHtmls = htmls
        payInContainer.append(div)
    } else {
        $('.bank-info-container').css({'display': 'none'})
    }

    if (payOutMethod !== null) {
        const {div, htmls} = attachPaymentFields(payOutMethod)
        payOutHtmls = htmls
        payOutContainer.append(div)
    } else {
        $(".bank-info-container-2").css({'display': 'none'})
    }

    const div = $('<div class="div"></div>');
    div.append('<label class="lab">You send: </label>')
    const sendInput = $(`<input class="currency-input send-input" placeholder="${payInCurrency}">`)
    div.append(sendInput)
    div.append('<label class="lab">You receive: </label>')
    const receiveInput = $(`<input class="currency-input" placeholder="${payOutCurrency}">`)
    div.append(receiveInput)
    conversionFields.append(div)

    sendInput.on('input', function() {
        let amount = parseFloat(sendInput.val());

        if (!isNaN(amount)) {
            let convertedAmount = amount * `${rate}`;
            receiveInput.val(convertedAmount.toFixed(2));
        } else {
            receiveInput.val('');
        }
    })



    $('.request-btn').on('click', function() {

        const payInDetails = payInHtmls.map(html => {
            const label = html.label
            const input = html.input
            return {
                fieldName: label.text(),
                fieldValue: input.val
            }
        })

        const payOutDetails = payOutHtmls.map(html => {
            const label = html.label
            const input = html.input
            return {
                fieldName: label.text(),
                fieldValue: input.val()
            }
        })

                payIn = {
                    paymentMethodKind: payInMethod == null? payInMethodKind : payInMethod.kind,
                    paymentMethodValues: []
                }

                payOut = {
                    paymentMethodKind: payOutMethod == null? payOutMethodKind : payOutMethod.kind,
                    paymentMethodValues: []
                }

                payInDetails.forEach(payInDetail => payIn.paymentMethodValues.push(payInDetail))
                payOutDetails.forEach(payOutDetail => payOut.paymentMethodValues.push(payOutDetail))

            const offeringRef = localStorage.getItem("offeringRef");
            const customerDID = localStorage.getItem("didUri");

            const data = {
                offeringRef: offeringRef,
                payin: payIn,
                payout: payOut,
                customerDID: customerDID,
                amount: sendInput.val()
            };


            $.ajax({
                url: 'http://localhost:8082/exchanges/rfqs',
                type: 'POST',
                data: JSON.stringify(data),
                contentType: 'application/json',
                success: function(response) {
                    console.log(response.exchangeId)
                    localStorage.setItem("exchangeId", response.exchangeId)
                    $('#rollerOverlay').css('display', 'flex');
                    $('.roller').show();
                    checkRfqProcessed(localStorage.getItem("exchangeId"))
                },
                error: function() {
                    console.log('Submission failed:');
                }
            });

        });

}


function attachPaymentFields(paymentMethod) {

    const htmls = []
    const div = $('<div class="div"></div>');
    for (const field of paymentMethod.paymentFields) {
        const label = $('<label class="label-in">' + field.fieldName + '</label>')
        const input = $(`<input class="input-in" type="text" required="${field.required}">`);
        div.append(label)
        div.append(input)
        const html = {label: label, input: input}
        htmls.push(html)
    }

    return {div, htmls}
}

function checkRfqProcessed(exchangeId) {
    const maxPollCount = 4
    let pollCount = 0;
    const intervalTime = 4000; // 4 seconds

    const intervalId = setInterval(function() {

            $.ajax({
                url: `http://localhost:8082/exchanges/rfqs/${exchangeId}/is-processed`,
                method: 'GET'
            })
                .done(function (response) {
                    if (response === true) {
                        clearInterval(intervalId);
                        window.location.href = "../quote"
                    }
                })
                .fail(function (xhr, status, error) {
                    console.error('Error in API call:', error);
                })
                .always(function () {
                    if (pollCount >= maxPollCount) {
                        console.log('Max poll count reached. Stopping.');
                        clearInterval(intervalId);
                    }
                });
            pollCount++;
    }, intervalTime);
}

