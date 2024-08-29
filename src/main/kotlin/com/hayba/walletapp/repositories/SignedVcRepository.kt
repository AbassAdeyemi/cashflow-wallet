package com.hayba.walletapp.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import com.hayba.walletapp.entities.SignedVc
import java.util.UUID

interface SignedVcRepository: MongoRepository<SignedVc, UUID> {
    fun findAllByDidUri(didUri: String): List<SignedVc>
}