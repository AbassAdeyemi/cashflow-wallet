package walletbackend.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import walletbackend.entities.Quote
import java.util.*

interface QuoteRepository: MongoRepository<Quote, UUID> {
    fun findByQuoteId(quoteId: String): Optional<Quote>
    fun findByExchangeId(exchangeId: String): Optional<Quote>
    fun findByExchangeIdAndOrderStatus(exchangeId: String, orderStatus: String): Optional<Quote>
}