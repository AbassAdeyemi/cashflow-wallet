const baseUrl = "http://localhost:8082";

$(document).ready(function() {
    var payInMethod = JSON.parse(localStorage.getItem('payInMethod'));
    var payOutMethod = JSON.parse(localStorage.getItem('payOutMethod'));
    var rate = localStorage.getItem("rate");
    var payInCurrency = localStorage.getItem("from");
    var payOutCurrency = localStorage.getItem("to");

    paymentMethodBody(payInMethod, payOutMethod, rate, payInCurrency, payOutCurrency);
})

function paymentMethodBody(payIn, payOut, rate, payInCurrency, payOutCurrency) {
    var payInContainer = $(".bank-info-container .bank-container");
    var payOutContainer = $(".bank-info-container-2 .bank-container-2");
    var conversionFields = $(".transaction-detail-container .transaction-detail")

    if(payIn !== null) {
        const div = attachPaymentFields(payIn)
        payInContainer.append(div)
    } else {
        $('.bank-info-container').css({'display': 'none'})
    }

    if(payOut !== null) {
        const div = attachPaymentFields(payOut)
        payOutContainer.append(div)
    } else {
        $(".bank-info-container-2").css({'display': 'none'})
    }


    function attachPaymentFields(paymentMethod) {
        const div = $('<div class="div"></div>');

        for (const field of paymentMethod.paymentFields) {
            div.append('<label class="label-in">' + field.fieldName + '</label>')
            div.append(`<input class="input-in" type="text", required="${field.required}">`);
        }
        return div
    }


    // conversion fields
    currenciesConversion();

    function currenciesConversion() {
        var div = $('<div class="div"></div>');
        var inputSend;
        var inputReceive;
        const currencies = [
            {
                "label": "You send:",
                "input": `${payInCurrency}`
            },
            {
                "label": "You receive:",
                "input": `${payOutCurrency}`
            }

        ]

        currencies.forEach((currency, index) => {

            div.append('<label class="lab">'+ currency.label +'</label>');
            var input = $(`<input placeholder="${currency.input}"></input>`).attr('type', 'number').addClass('currency-input');

            if (index === 0) {
                inputSend = input;
                inputSend.addClass("send-input")
            } else if (index === 1) {
                inputReceive = input;
                inputReceive.prop('readonly', true);
                // inputReceive.addClass('readonly-input');
            }

            div.append(input);
        })

        conversionFields.append(div);

        inputSend.on('input', function() {
            let amount = parseFloat(inputSend.val());

            if (!isNaN(amount)) {
                let convertedAmount = amount * `${rate}`;
                inputReceive.val(convertedAmount.toFixed(2));
            } else {
                inputReceive.val('');
            }
        });


        $('.request-btn').on('click', function() {

            var paymentInMethodKind = `${payInCurrency}`;
            var paymentOutMethodKind = `${payOutCurrency}`;
            var amount = inputSend.val();
            var fieldNameIn = $('.input-in').val();
            var fieldNameOut = $('.input-out').val();
            var labelIn = $('label-in')
            var labelOut = $('label-out')

            var offeringRef = localStorage.getItem("offeringRef");
            var customerDID = localStorage.getItem("didUri");

            var data = {
                offeringRef: offeringRef,
                payin: {
                    paymentMethodKind: paymentInMethodKind,
                    paymentMethodValues: [
                        {
                            fieldName: labelIn,
                            fieldValue: fieldNameIn
                        },
                        {
                            fieldName: labelIn,
                            fieldValue: fieldNameIn
                        }
                    ]
                },
                payout: {
                    paymentMethodKind: paymentOutMethodKind,
                    paymentMethodValues: [
                        {
                            fieldName: labelOut,
                            fieldValue: fieldNameOut
                        },
                        {
                            fieldName: labelOut,
                            fieldValue: fieldNameOut
                        }
                    ]
                },
                customerDID: customerDID,
                amount: amount
            };
            // window.location.href = '/quote.html';

            console.log(data.payin);
            $.ajax({
                url: 'http://localhost:8082/exchanges/rfqs',
                type: 'POST',
                data: JSON.stringify(data),
                contentType: 'application/json',
                success: function(response) {
                    console.log(response);

                },
                error: function() {
                    console.log('Submission failed:');
                }
            });

            function lazyLoadPage() {
                $('request-btn').on('click', function (

                ))
            }

        });

    }

}
