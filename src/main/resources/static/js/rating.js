$(document).ready(function () {

    let selectedRating = 0;

    $(".rating-star").on("click", function() {
        selectedRating = $(this).data('value');
        highlightStars(selectedRating);
    });

    function highlightStars(rating) {
        $(".rating-star").each(function() {
            $(this).removeClass("filled");
            if ($(this).data('value') <= rating) {
                $(this).addClass("filled");
            }
        });
    }

    $("#submit-rating").on("click", function() {

        const customerDID = localStorage.getItem("didUri");
        const pfiDID = localStorage.getItem("pfiDID")
        const exchangeId = localStorage.getItem("exchangeId")

        const data = {
           pfiDID: pfiDID,
           rating: selectedRating,
           exchangeId: exchangeId,
            customerDID: customerDID
        }

        if (selectedRating === 0) {
            alert("Please select a rating star");
        } else {

            $.ajax({
                url: 'http://localhost:8082/ratings',
                type: 'POST',
                data: JSON.stringify(data),
                contentType: 'application/json',
                success: function() {
                    setTimeout(() => {
                        alert("Rating submitted successfully!");
                    }, 6000)
                    window.location.href = "../dashboard"
                }
            });
        }
    });
})