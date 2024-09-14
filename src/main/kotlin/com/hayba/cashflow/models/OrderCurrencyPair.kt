package com.hayba.cashflow.models


data class OfferingMatchRequest(
        val userId: String,
        val orderCurrencyPair: OrderCurrencyPair
)
data class OrderCurrencyPair(val fromCurrency: String, val toCurrency: String)