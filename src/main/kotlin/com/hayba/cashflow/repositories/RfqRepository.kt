package com.hayba.cashflow.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import com.hayba.cashflow.entities.Rfq
import com.hayba.cashflow.models.ExchangeStatus
import java.util.*

interface RfqRepository : MongoRepository<Rfq, UUID> {
    fun findByExchangeId(exchangeId: String): Optional<Rfq>

    @Query("{'exchangeId' : ?0}")
    @Update("{'\$set': {'exchangeStatus': ?1}}")
    fun updateExchangeStatus(exchangeId: String, status: ExchangeStatus)

    fun findAllByExchangeStatusIn(status: List<ExchangeStatus>): List<Rfq>
}