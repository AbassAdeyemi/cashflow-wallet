package walletbackend.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import walletbackend.entities.SignedVc
import java.util.UUID

interface SignedVcRepository: MongoRepository<SignedVc, UUID> {
    fun findAllByDidUri(didUri: String): List<SignedVc>
}