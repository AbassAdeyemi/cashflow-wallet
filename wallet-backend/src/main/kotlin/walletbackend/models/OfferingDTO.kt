package walletbackend.models

import java.math.BigDecimal

data class OfferingDTO(
        val pfiDID: String,
        val pfiName: String,
        val rate: BigDecimal,
        val payInMethod: List<PaymentMethod>,
        val payOutMethod: List<PaymentMethod>,
        val payInCurrency: String,
        val payOutCurrency: String,
        val offeringID: String
)

data class PaymentMethod(
        val kind: String,
        val paymentFields: List<PaymentField>
)

data class PaymentField(
        val fieldName: String,
        val required: Boolean
)
