package com.hayba.cashflow.models

import java.math.BigDecimal

data class CreateRfQRequest(
        val offeringRef: String,
        val payin: PaymentDetail,
        val payout: PaymentDetail,
        val customerDID: String,
        val amount: BigDecimal
)


data class PaymentDetail(
        val paymentMethodKind: String,
        val paymentMethodValues: List<PaymentMethodValue>
)

data class PaymentMethodValue(
        val fieldName: String,
        val fieldValue: String
)
