// Available Currency pairs
$(document).ready(function(){
    var currencyApi = `/offerings/currency-pairs`;
    var url = baseUrl + currencyApi;

    currencyPairs(url);
})

function currencyPairs(url) {
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            createTable(data);
        }
    })

}

function createTable(items) {
    var $tableBody = $('#currency-table tbody');
    $tableBody.empty();

    for (let i = 0; i < 5; i++) {
        const off = items[i];

        var row = $('<tr></tr>');

        row.append('<td>' + off.fromCurrency + '</td>');
        row.append('<td>' + off.toCurrency + '</td>');

        var button = $(`<button class="view-btn" data-from="${off.fromCurrency}" data-to="${off.toCurrency}">View trade</button>`);

        var buttonCell = $('<td></td>');
        buttonCell.append(button);
        row.append(buttonCell);

        $tableBody.append(row);
    }
}


$('#currency-table tbody').on('click', '.view-btn', function() {
    const fromCurrency = $(this).data('from');
    const toCurrency = $(this).data('to');


    localStorage.setItem("from", fromCurrency);
    localStorage.setItem("to", toCurrency);
    window.location.href = '../offering.html';

    const $resultContainer = $('#resultContainer');

    // localStorage.setItem('firstLoadDone', 'true');

});
