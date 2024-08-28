package walletbackend.models

import walletbackend.entities.PaymentField
import walletbackend.entities.PaymentMethod
import java.math.BigDecimal

data class OfferingDTO(
        val id: String,
        val pfiName: String,
        val rate: BigDecimal,
        val payInMethods: List<PaymentMethod>,
        val payOutMethods: List<PaymentMethod>,
        val payInCurrency: String,
        val payOutCurrency: String,
        val ref: String
)


