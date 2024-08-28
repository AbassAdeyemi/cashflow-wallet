package walletbackend.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import walletbackend.entities.Offering
import java.util.*

interface OfferingRepository: MongoRepository<Offering, UUID> {
    fun findByPayInCurrencyAndPayOutCurrency(payInCurrency: String, payOutCurrency: String): List<Offering>
    fun findByRef(ref: String): Optional<Offering>
}