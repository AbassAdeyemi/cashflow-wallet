package walletbackend.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import walletbackend.models.PaymentMethod
import java.math.BigDecimal
import java.util.UUID

@Document
data class OfferingEntity(
        @Id
        val id: UUID,
        val pfiDID: String,
        val pfiName: String,
        val rate: BigDecimal,
        val payInMethod: List<PaymentMethod>,
        val payOutMethod: List<PaymentMethod>,
        val payInCurrency: String,
        val payOutCurrency: String,
        @Indexed
        val offeringID: String,
)


