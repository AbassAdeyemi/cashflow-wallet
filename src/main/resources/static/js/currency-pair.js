// Available Currency pairs
$(document).ready(function(){
    const currencyApi = `/offerings/currency-pairs`;
    const url = baseUrl + currencyApi;

    currencyPairs(url);
})

let searchItems = []
function currencyPairs(url) {
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            searchItems = data
            createTable(searchItems);
        }
    })

}

function createTable(items) {
    const tableBody = $('#currency-table tbody');
    tableBody.empty();

    for (const item of items) {
        const row = $('<tr></tr>');

        row.append('<td>' + item.fromCurrency + '</td>');
        row.append('<td>' + item.toCurrency + '</td>');

        const button = $(`<button class="view-btn" data-from="${item.fromCurrency}" 
        data-to="${item.toCurrency}">View Offerings</button>`);

        const buttonCell = $('<td></td>');
        buttonCell.append(button);
        row.append(buttonCell);

        tableBody.append(row);
    }
}


$('#currency-table tbody').on('click', '.view-btn', function() {
    const fromCurrency = $(this).data('from');
    const toCurrency = $(this).data('to');


    localStorage.setItem("from", fromCurrency);
    localStorage.setItem("to", toCurrency);
    window.location.href = '/cashflow/offering';

});

const fromInput = $('#from-input')
const toInput = $('#to-input')

fromInput.on('keydown', function(event) {
    const fromCurrency = $(this).val()
    const toCurrency = toInput.val()
    let filtered
    if (event.key === 'Enter') {
        if(fromCurrency.trim() !== '' && toCurrency !== '') {
            filtered = searchItems.filter(searchItem => searchItem.fromCurrency.toLowerCase() === fromCurrency.toLowerCase()
            && searchItem.toCurrency.toLowerCase() === toCurrency.toLowerCase())
            createTable(filtered)
        } else if(fromCurrency.trim() !== '') {
            filtered = searchItems.filter(searchItem => searchItem.fromCurrency.toLowerCase() === fromCurrency.toLowerCase())
            createTable(filtered)
        } else {
            createTable(searchItems)
        }
    }
});

toInput.on('keydown', function(event) {
    const toCurrency = $(this).val()
    const fromCurrency = fromInput.val()
    let filtered
    if (event.key === 'Enter') {
        if(toCurrency.trim() !== '' && fromCurrency !== '') {
            filtered = searchItems.filter(searchItem => searchItem.toCurrency.toLowerCase() === toCurrency.toLowerCase()
                && searchItem.toCurrency.toLowerCase() === toCurrency.toLowerCase())
            createTable(filtered)
        } else if(toCurrency.trim() !== '') {
            filtered = searchItems.filter(searchItem => searchItem.fromCurrency.toLowerCase() === fromCurrency.toLowerCase())
            createTable(filtered)
        } else {
            createTable(searchItems)
        }

    }
});


