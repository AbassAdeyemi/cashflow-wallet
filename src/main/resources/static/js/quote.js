$(document).ready(function(){
    const customerDID = localStorage.getItem('didUri');
    const exchangeId = localStorage.getItem("exchangeId")
    const url = baseUrl + `/exchanges/quotes/${customerDID}`;

    existingQuote(url, exchangeId);
})

function existingQuote(url, exchangeId) {
    $.ajax({
         url: url,
        type: 'GET',
        success: function (data) {
            const quote = data.find(item => item.exchangeId === exchangeId)
             if(exchangeId && quote) {
                 localStorage.setItem("pfiDID", quote.pfiDID)
                 displayLatestQuote(quote)
             }
             const rest = data.filter(item => item.exchangeId !== exchangeId)
            existingQuoteTable(rest);
        }
    })
}

function displayLatestQuote(quote) {
    $('.quote-container').removeClass('hidden')
    const pTags = []
    console.log(quote)
    pTags.push($('<p></p>').text('Amount To Pay:' + quote.payin.amount));
    pTags.push($('<p></p>').text('Fee:' + quote.payin.fee))
    pTags.push($('<p></p>').text('Your Currency:' + quote.payin.currencyCode))
    pTags.push($('<p></p>').text('Amount To Receive:' + quote.payout.amount))
    pTags.push($('<p></p>').text('Pfi Currency:' + quote.payout.currencyCode))
    if(quote.payin.paymentInstruction) {
        if(quote.payin.paymentInstruction?.link) {
            pTags.push($('<p></p>').text('Payment Link:' + quote.payin.paymentInstruction.link))
        }
        if(quote.payin.paymentInstruction?.instruction) {
            pTags.push($('<p></p>').text('Payment Instruction:' + quote.payin.paymentInstruction.instruction))
        }
    }

      pTags.forEach(pTag => $('.quote').append(pTag))
}

function existingQuoteTable(items) {
    const $tableBody = $('.existing-quote-container tbody');
    $tableBody.empty();

    for (const item of items) {
        let date = new Date(item.expiresAt);
        let formattedDate = date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'numeric',
            day: 'numeric'
        });
        const row = $('<tr></tr>');

        row.append('<td>' + item.payin.amount + '</td>');
        row.append('<td>' + item.payin.currencyCode + '</td>');
        row.append('<td>' + item.payout.amount + '</td>');
        row.append('<td>' + item.payout.currencyCode + '</td>')
        row.append('<td>' + formattedDate + '</td>')
        row.append('<td>' + item.quoteStatus + '</td>')

        const button = $(`<button class="complete" data-payinAmount="${item.payin.amount}" 
         data-payinCurrency="${item.payin.currencyCode}"
            data-payoutAmount="${item.payout.amount}" 
            data-payoutCurrency="${item.payout.currencyCode}"
            data-exchangeId="${item.exchangeId}"
            data-pfiDID="${item.pfiDID}"
            data-expiry="${formattedDate}"
            data-fee="${item.payin.fee}"
            data-paymentLink="${item.payin.paymentInstruction.link}"
            data-paymentInstruction="${item.payin.paymentInstruction.instruction}">Complete</button>`);
        const buttonCell = $('<td></td>');
        buttonCell.append(button);
        row.append(buttonCell);

        $tableBody.append(row);
    }
}

  $('.existing-quote-container tbody ').on('click', '.complete', function(){
      const quote = {
          payin: {
              amount: $(this).data('payinamount'),
              currencyCode: $(this).data('payincurrency'),
              fee: $(this).data('fee'),
              paymentInstruction: {
                  link: $(this).data('paymentlink'),
                  instruction: $(this).data('paymentinstruction')
              }
          },
          payout: {
              amount: $(this).data('payoutamount'),
              currencyCode: $(this).data('payoutcurrency')
          },
          expiry: $(this).data('expiry')
      }

      const exchangeId = $(this).data('exchangeid')
      const pfiDID = $(this).data('pfidid')
      localStorage.setItem("exchangeId", exchangeId)
      localStorage.setItem("pfiDID", pfiDID)

      $('.quote').empty()
      displayLatestQuote(quote)
  })

const quoteLink = $('.quote-link')

quoteLink.on('click', '.proceed', function (){

    $('#rollerOverlay').css('display', 'flex');
    $('.roller').show();
    const data = {
        exchangeId: localStorage.getItem("exchangeId"),
        proceed: true
    }

    $.ajax({
        url: 'http://localhost:8082/exchanges/quotes',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json',
        success: function() {
            checkOrderCompleted(data.exchangeId)
        },
        error: function() {
            console.log('Submission failed:');
        }
    });

});

quoteLink.on('click', '.cancel', function (){
    const data = {
        exchangeId: localStorage.getItem("exchangeId"),
        proceed: false
    }

    $.ajax({
        url: 'http://localhost:8082/exchanges/quotes',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json',
        success: function() {
           window.location.href = '../dashboard'
        },
        error: function() {
            console.log('Submission failed:');
        }
    });

});


function checkOrderCompleted(customerDID) {
    const maxPollCount = 4
    let pollCount = 0;
    const intervalTime = 4000; // 4 seconds

    const intervalId = setInterval(function() {

            $.ajax({
                url: `http://localhost:8082/exchanges/quotes/${customerDID}`,
                method: 'GET'
            })
                .done(function (response) {
                    const exchangeId = localStorage.getItem("exchangeId")
                    if (isOrderCompleted(response, exchangeId)) {
                        console.log("Order Completed !!!")
                        clearInterval(intervalId);
                        window.location.href = "../rating"
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

const isOrderCompleted = (items, exchangeId) => {
    return !items.some(item => item.exchangeId === exchangeId)
}