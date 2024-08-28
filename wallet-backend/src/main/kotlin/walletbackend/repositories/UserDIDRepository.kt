package walletbackend.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import walletbackend.entities.UserDID
import java.util.*

interface UserDIDRepository: MongoRepository<UserDID, UUID> {
    fun findByDidUri(didUri: String): Optional<UserDID>
}