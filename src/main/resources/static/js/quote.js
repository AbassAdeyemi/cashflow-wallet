const baseUrl = "http://localhost:8082";

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
            const quote = data.filter(item => item.exchangeId === exchangeId)
             if(exchangeId !== null && quote !== null) {
                 displayLatestQuote(data)
             }
            existingQuoteTable(data);
        }
    })
}

function displayLatestQuote(quote) {
    const pTags = []
    pTags.push($('<p></p>').text('Amount To Pay:' + quote.payin.amount));
    pTags.push($('<p></p>').text('Fee:' + quote.payin.fee))
    pTags.push($('<p></p>').text('Your Currency:' + quote.payin.currencyCode))
    pTags.push($('<p></p>').text('Amount To Receive:' + quote.payout.amount))
    pTags.push($('<p></p>').text('Pfi Currency:' + quote.payout.currencyCode))
    if(quote.payin.paymentInstruction !== null) {
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
            data-expiry="${formattedDate}"
            data-fee="${item.payin.fee}"
            data-paymentLink="${item.payin.paymentInstruction.link}"
            data-paymentInstruction="${item.payin.paymentInstruction.instruction}">Complete</button>`);
        console.log(button)
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

      $('.quote-container').removeClass('hidden')
      displayLatestQuote(quote)
  })