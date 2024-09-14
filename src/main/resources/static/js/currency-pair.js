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

$('.from-input').on('keydown', function(event) {
    const fromCurrency = $(this).val()
    if (event.key === 'Enter') {
       searchItems = searchItems.filter(searchItem => searchItem.fromCurrency.toLowerCase() === fromCurrency.toLowerCase())
        createTable(searchItems)
    }
});

$('.to-input').on('keydown', function(event) {
    const toCurrency = $(this).val()
    if (event.key === 'Enter') {
        searchItems = searchItems.filter(searchItem => searchItem.toCurrency.toLowerCase() === toCurrency.toLowerCase())
        createTable(searchItems)
    }
});


