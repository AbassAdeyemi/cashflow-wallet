package com.hayba.walletapp.controllers

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.hayba.walletapp.entities.Quote
import com.hayba.walletapp.models.CreateRfQRequest
import com.hayba.walletapp.models.QuoteNextStep
import com.hayba.walletapp.models.RfqCreationResponse
import com.hayba.walletapp.services.ExchangeService

@RestController
@RequestMapping("/exchanges")
class ExchangeController(private val exchangeService: ExchangeService) {

    @PostMapping("/rfqs")
    fun createRfq(@RequestBody createRfQRequest: CreateRfQRequest): ResponseEntity<RfqCreationResponse> {
        val rfqResponse = exchangeService.createRfq(createRfQRequest)
        return ResponseEntity.ok(rfqResponse)
    }

    @GetMapping("/rfqs/{exchangeId}/is-processed")
    fun isRfqProcessed(@PathVariable exchangeId: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(exchangeService.isRfqProcessed(exchangeId))
    }

    @PostMapping("/quotes")
    fun processQuote(@RequestBody quoteNextStep: QuoteNextStep): ResponseEntity<Void> {
        exchangeService.processQuote(quoteNextStep)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/quotes/{didUri}")
    fun existingQuote(@PathVariable didUri: String): List<Quote> {
        return exchangeService.existingQuotes(didUri)
    }

    @GetMapping("/quotes/{didUri}/history")
    fun orderHistory(@PathVariable didUri: String, pageable: Pageable): Page<Quote> {
        return exchangeService.orderHistory(didUri, pageable)
    }
}