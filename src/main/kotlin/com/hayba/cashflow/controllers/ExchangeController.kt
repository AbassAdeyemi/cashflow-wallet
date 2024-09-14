package com.hayba.cashflow.controllers

import com.hayba.cashflow.entities.Quote
import com.hayba.cashflow.models.CreateRfQRequest
import com.hayba.cashflow.models.QuoteNextStep
import com.hayba.cashflow.models.RfqCreationResponse
import com.hayba.cashflow.services.ExchangeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    fun orderHistory(@PathVariable didUri: String): List<Quote> {
        return exchangeService.orderHistory(didUri)
    }
}