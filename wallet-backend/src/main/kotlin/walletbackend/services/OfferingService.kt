package walletbackend.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tbdex.sdk.protocol.models.Offering
import walletbackend.exceptions.OfferingNotFoundException
import walletbackend.models.OfferingDTO
import walletbackend.models.OrderCurrencyPair
import walletbackend.repositories.OfferingRepository
import walletbackend.repositories.PfiRepository
import walletbackend.repositories.SignedVcRepository
import web5.sdk.credentials.PresentationExchange
import java.util.UUID

@Service
class OfferingService(private val pfiRepository: PfiRepository,
                      private val offeringRepository: OfferingRepository,
private val signedVcRepository: SignedVcRepository, private val objectMapper: ObjectMapper) {
    companion object {
        private val log = LoggerFactory.getLogger(OfferingService::class.java)
    }

    fun getAvailablePairs(): List<OrderCurrencyPair> {
        return pfiRepository.findAll().flatMap { it.offeringCurrencyPairs }
                .map { OrderCurrencyPair(fromCurrency = it.fromCurrency, toCurrency = it.toCurrency) }.distinct()
    }

    fun getMatchingOfferings(currencyPair: OrderCurrencyPair): List<OfferingDTO> {
        val (from, to) = currencyPair
        val offerings = offeringRepository.findByPayInCurrencyAndPayOutCurrency(from, to)

        return offerings.map {
            OfferingDTO(
                    id = it.id.toString(),
                    pfiName = it.pfiName,
                    rate = it.rate,
                    payInMethods = it.payInMethods,
                    payOutMethods = it.payOutMethods,
                    payInCurrency = it.payInCurrency,
                    payOutCurrency = it.payOutCurrency,
                    ref = it.ref
            )
        }
    }

    fun hasMatchingCredential(didUri: String, offeringRef: String): Boolean {
        val vcs = signedVcRepository.findAllByDidUri(didUri)
        val optionalOffering = offeringRepository.findByRef(offeringRef)
        if (optionalOffering.isEmpty) {
            throw OfferingNotFoundException("Ref: $offeringRef invalid, Please choose another offering with a valid ref")
        }
        val offering = optionalOffering.get()
        val externalOffering = objectMapper.readValue(offering.jsonString, Offering::class.java)
        return try {
            externalOffering.data.requiredClaims?.let {
                PresentationExchange.satisfiesPresentationDefinition(
                        vcJwts = vcs.map { vc -> vc.signedJWT },
                        presentationDefinition = it
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }

}