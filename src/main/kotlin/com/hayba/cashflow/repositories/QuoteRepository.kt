package com.hayba.cashflow.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import com.hayba.cashflow.entities.Quote
import com.hayba.cashflow.models.QuoteStatus
import java.time.LocalDateTime
import java.util.*

interface QuoteRepository: MongoRepository<Quote, UUID> {
    fun findAllByCustomerDIDAndQuoteStatusIn(customerDID: String, quoteStatuses: List<QuoteStatus>, pageable: Pageable): Page<Quote>
    fun findByExchangeId(exchangeId: String): Optional<Quote>
    fun findByExchangeIdAndOrderStatus(exchangeId: String, orderStatus: String): Optional<Quote>

    @Query("{'exchangeId' : ?0}")
    @Update("{'\$set': {'quoteStatus': ?1, 'completedAt': ?2}}")
    fun updateQuoteStatus(exchangeId: String, status: QuoteStatus, completedAt: LocalDateTime? = null)

}