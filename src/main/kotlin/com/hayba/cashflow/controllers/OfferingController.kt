package com.hayba.cashflow.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import com.hayba.cashflow.models.OfferingDTO
import com.hayba.cashflow.models.OrderCurrencyPair
import com.hayba.cashflow.services.OfferingService

@RestController
@RequestMapping("/offerings")
class OfferingController(private val offeringService: OfferingService) {

    @GetMapping("/currency-pairs")
    fun availableCurrencyPairs(): List<OrderCurrencyPair>{
      return offeringService.getAvailablePairs()
    }

    @GetMapping("/match")
    fun matchingOfferings(@RequestParam fromCurrency:String, @RequestParam toCurrency: String): List<OfferingDTO> {
        return offeringService.getMatchingOfferings(currencyPair = OrderCurrencyPair(fromCurrency, toCurrency))
    }

    @GetMapping("/has-credential")
    fun hasMatchingCredential(@RequestParam didUri: String,
                              @RequestParam offeringRef: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(offeringService.hasMatchingCredential(didUri, offeringRef))
    }
}