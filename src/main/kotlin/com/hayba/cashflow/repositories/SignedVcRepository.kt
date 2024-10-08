package com.hayba.cashflow.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import com.hayba.cashflow.entities.SignedVc
import java.util.UUID

interface SignedVcRepository: MongoRepository<SignedVc, UUID> {
    fun findAllByDidUri(didUri: String): List<SignedVc>
}