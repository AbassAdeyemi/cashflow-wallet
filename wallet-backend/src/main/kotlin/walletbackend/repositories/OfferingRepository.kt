package walletbackend.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import walletbackend.entities.OfferingEntity
import java.util.*

interface OfferingRepository: MongoRepository<OfferingEntity, UUID> {
    fun findByPayInCurrencyAndPayOutCurrency(payInCurrency: String, payOutCurrency: String): List<OfferingEntity>
}