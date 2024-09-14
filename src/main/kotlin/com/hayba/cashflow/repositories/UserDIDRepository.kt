package com.hayba.cashflow.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import com.hayba.cashflow.entities.UserDID
import java.util.*

interface UserDIDRepository: MongoRepository<UserDID, UUID> {
    fun findByDidUri(didUri: String): Optional<UserDID>
}