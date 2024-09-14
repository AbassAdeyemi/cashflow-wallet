package com.hayba.cashflow.models

data class RatingRequest(
        val customerDID: String,
        val pfiDID: String,
        val exchangeId: String,
        val rating: Int
)
