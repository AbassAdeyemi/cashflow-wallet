package com.hayba.walletapp.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import com.hayba.walletapp.entities.Quote
import com.hayba.walletapp.models.QuoteStatus
import java.util.*

interface QuoteRepository: MongoRepository<Quote, UUID> {
    fun findAllByCustomerDIDAndQuoteStatusIn(customerDID: String, quoteStatuses: List<QuoteStatus>, pageable: Pageable): Page<Quote>
    fun findByExchangeId(exchangeId: String): Optional<Quote>
    fun findByExchangeIdAndOrderStatus(exchangeId: String, orderStatus: String): Optional<Quote>

    @Query("{'exchangeId' : ?0}")
    @Update("{'\$set': {'quoteStatus': ?1}}")
    fun updateQuoteStatus(exchangeId: String, status: QuoteStatus)
}