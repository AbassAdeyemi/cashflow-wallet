package com.hayba.walletapp.models

data class RatingRequest(
        val customerDID: String,
        val pfiDID: String,
        val exchangeId: String,
        val rating: Int
)
