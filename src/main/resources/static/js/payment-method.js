$(document).ready(function() {
    const payInMethod = getPaymentMethod('payInMethod');
    const payOutMethod = getPaymentMethod('payOutMethod');
    const rate = localStorage.getItem("rate");
    const payInCurrency = localStorage.getItem("from");
    const payOutCurrency = localStorage.getItem("to");

    paymentMethodBody(payInMethod, payOutMethod, rate, payInCurrency, payOutCurrency);
})

function paymentMethodBody(payInMethod, payOutMethod, rate, payInCurrency, payOutCurrency) {
    const payInContainer = $(".bank-info-container .bank-container");
    const payOutContainer = $(".bank-info-container-2 .bank-container-2");
    const amountFields = $(".transaction-detail-container .transaction-detail")

    let payInHtmls = []
    let payOutHtmls = []
    if (payInMethod && typeof payInMethod === 'object') {
        payInHtmls = attachPaymentFields(payInMethod)
        const div = $('<div class="div"></div>');
        payInHtmls.forEach(payInHtml => {
            div.append(payInHtml.label)
            div.append(payInHtml.input)
        })
        payInContainer.append(div)
    } else {
        $('.bank-info-container').css({'display': 'none'})
    }

    if (payOutMethod && typeof payOutMethod === 'object') {
        payOutHtmls = attachPaymentFields(payOutMethod)
        const div = $('<div class="div"></div>');
        payOutHtmls.forEach(payOutHtml => {
            div.append(payOutHtml.label)
            div.append(payOutHtml.input)
        })
        payOutContainer.append(div)
    } else {
        $(".bank-info-container-2").css({'display': 'none'})
    }

    const div = $('<div class="div"></div>');
    div.append('<label class="lab">You will send: </label>')
    const sendInput = $(`<input class="currency-input send-input" type="number" placeholder="${payInCurrency}" required>`)
    div.append(sendInput)
    div.append('<label class="lab">You will receive: </label>')
    const receiveInput = $(`<input class="currency-input" placeholder="${payOutCurrency}">`)
    div.append(receiveInput)
    amountFields.append(div)

    sendInput.on('input', function() {
        let amount = parseFloat(sendInput.val());

        if (!isNaN(amount)) {
            let convertedAmount = amount * `${rate}`;
            receiveInput.val(convertedAmount.toFixed(2));
        } else {
            receiveInput.val('');
        }
    })



    $('.request-btn').on('click', function(e) {

        const errorHtml = $('.error-html')
        $('.error-html p').empty()
        const validationErrors = getValidationErrors(payInHtmls, payOutHtmls, sendInput)
        if(validationErrors.length !== 0) {
            validationErrors.forEach(err => {
               errorHtml.append(`<p>${err}</p>`)
            })
           errorHtml.removeClass('hidden')
        }
        else {
            const payInDetails = payInHtmls.map(html => {
                const label = html.label
                const input = html.input
                return {
                    fieldName: label.text(),
                    fieldValue: input.val()
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

            const payInMethodKind = typeof payInMethod === 'string'? payInMethod : payInMethod.kind
            const payOutMethodKind = typeof  payOutMethod === 'string'? payOutMethod : payOutMethod.kind

            const payIn = {
                paymentMethodKind: payInMethodKind,
                paymentMethodValues: []
            }

            const payOut = {
                paymentMethodKind: payOutMethodKind,
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

            console.log(data)


            $.ajax({
                url: baseUrl + '/exchanges/rfqs',
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
        }
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

    return htmls
}

function checkRfqProcessed(exchangeId) {
    const maxPollCount = 5
    let pollCount = 0;
    const intervalTime = 5000; // 5 seconds

    const intervalId = setInterval(function() {

            $.ajax({
                url: `${baseUrl}/exchanges/rfqs/${exchangeId}/is-processed`,
                method: 'GET'
            })
                .done(function (response) {
                    if (response === true) {
                        clearInterval(intervalId);
                        window.location.href = "/cashflow/quote"
                    }
                })
                .fail(function (xhr, status, error) {
                    console.error('Error in API call:', error);
                })
                .always(function () {
                    if (pollCount >= maxPollCount) {
                        console.log('Max poll count reached. Stopping.');
                        clearInterval(intervalId);
                        hideRollerAndDisplayError()
                    }
                });
            pollCount++;
    }, intervalTime);
}

function getPaymentMethod(key) {
    const paymentMethod = localStorage.getItem(key);
    try {
        const parsedPaymentMethod = JSON.parse(paymentMethod);

        if (parsedPaymentMethod && typeof parsedPaymentMethod === 'object') {
            return parsedPaymentMethod;
        }
        return paymentMethod;
    } catch (e) {
        return paymentMethod;
    }
}

function hideRollerAndDisplayError() {
    $('#rollerOverlay').css('display', 'none');
    $('.roller').hide()
    $('.error-html p').text('Error occurred. Could not request for quote')
    $('.error-html').removeClass('hidden')
}

function getValidationErrors(payInHtmls, payOutHtmls, sendInput) {
    const errorMessages = []

    payInHtmls.forEach(payInHtml => {
        if(payInHtml.input.attr('required') && payInHtml.input.val().trim() === "") {
            errorMessages.push(`payin ${payInHtml.label.text()} is required` )
        }
    })

    payOutHtmls.forEach(payOutHtml => {
        if(payOutHtml.input.attr('required') && payOutHtml.input.val().trim() === "") {
            errorMessages.push(`payin ${payOutHtml.label.text()} is required` )
        }
    })

    if ( sendInput.val().trim() === '' || isNaN(parseFloat(sendInput.val())) || sendInput.val() < 0) {
        errorMessages.push('Please enter a valid payin amount')
    }

    return errorMessages
}

$('#error-cancel').on('click', () => {
    $('.error-html').addClass('hidden')
})
