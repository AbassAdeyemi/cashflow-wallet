package walletbackend.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import walletbackend.models.CreateRfQRequest
import walletbackend.models.QuoteNextStep
import walletbackend.models.RfqCreationResponse
import walletbackend.services.ExchangeService

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
}