package walletbackend.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import walletbackend.models.ExchangeStatus
import java.util.*

@Document
data class Rfq(
        @Id
        val id: UUID,
        val customerDID: String,
        val pfiDID: String,
        val exchangeId: String,
        val exchangeStatus: ExchangeStatus = ExchangeStatus.RFQ_CREATION_PENDING
)
