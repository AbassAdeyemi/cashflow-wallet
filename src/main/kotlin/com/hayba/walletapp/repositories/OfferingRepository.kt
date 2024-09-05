package com.hayba.walletapp.repositories

import com.hayba.walletapp.entities.Offering
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface OfferingRepository: MongoRepository<Offering, UUID> {
    @Query(value = "{ 'payInCurrency' : ?0, 'payOutCurrency': ?1, 'stale': false }")
    fun findByCurrencyPairs(payInCurrency: String, payOutCurrency: String): List<Offering>

    @Query(value = "{'ref': ?0, 'stale': false}")
    fun findByRef(ref: String): Optional<Offering>

    @Query(value = "{ 'stale' : false }", fields = "{ 'ref' : 1 }")
    fun findAllReferences(): Set<String>

    fun findAllByStaleIsFalse(): List<Offering>
}