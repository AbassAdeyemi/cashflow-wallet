package com.hayba.walletapp.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import com.hayba.walletapp.entities.UserDID
import java.util.*

interface UserDIDRepository: MongoRepository<UserDID, UUID> {
    fun findByDidUri(didUri: String): Optional<UserDID>
}