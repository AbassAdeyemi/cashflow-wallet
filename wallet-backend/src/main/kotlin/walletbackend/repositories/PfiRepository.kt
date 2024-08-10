package walletbackend.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import walletbackend.entities.Pfi
import java.util.UUID

interface PfiRepository: MongoRepository<Pfi, UUID> {
}