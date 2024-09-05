package com.hayba.walletapp.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document
data class Offering(
        @Id
        val id: UUID,
        val pfiDID: String,
        val pfiName: String,
        val rate: BigDecimal,
        val payInMethods: List<PaymentMethod>,
        val payOutMethods: List<PaymentMethod>,
        val payInCurrency: String,
        val payOutCurrency: String,
        val ref: String,
        val jsonString: String,
        val stale: Boolean = false
)

data class PaymentMethod(
        val kind: String,
        val paymentFields: List<PaymentField>
)

data class PaymentField(
        val fieldName: String,
        val required: Boolean
)


