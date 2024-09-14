package com.hayba.cashflow.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import com.hayba.cashflow.entities.Pfi
import java.util.*

interface PfiRepository: MongoRepository<Pfi, UUID> {
    fun findByDidUri(didUri: String): Pfi?
}