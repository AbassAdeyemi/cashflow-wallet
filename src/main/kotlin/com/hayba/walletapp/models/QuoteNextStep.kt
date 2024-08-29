package com.hayba.walletapp.models

data class QuoteNextStep(
        val exchangeId: String,
        val proceed: Boolean,
        val reason: String? = ""
)
