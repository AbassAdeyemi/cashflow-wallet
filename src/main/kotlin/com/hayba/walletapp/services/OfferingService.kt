package com.hayba.walletapp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.hayba.walletapp.exceptions.OfferingNotFoundException
import com.hayba.walletapp.models.OfferingDTO
import com.hayba.walletapp.models.OrderCurrencyPair
import com.hayba.walletapp.repositories.OfferingRepository
import com.hayba.walletapp.repositories.PfiRepository
import com.hayba.walletapp.repositories.SignedVcRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Service
import tbdex.sdk.protocol.models.Offering
import web5.sdk.credentials.PresentationExchange

@Service
class OfferingService(
    private val pfiRepository: PfiRepository,
    private val offeringRepository: OfferingRepository,
    private val signedVcRepository: SignedVcRepository,
    private val objectMapper: ObjectMapper,
    private val mongoTemplate: MongoTemplate
) {

    private val log = LoggerFactory.getLogger(OfferingService::class.java)

    fun getAvailablePairs(): List<OrderCurrencyPair> {
        return pfiRepository.findAll().flatMap { it.offeringCurrencyPairs }
            .map { OrderCurrencyPair(fromCurrency = it.fromCurrency, toCurrency = it.toCurrency) }.distinct()
    }

    fun getMatchingOfferings(currencyPair: OrderCurrencyPair): List<OfferingDTO> {
        val (from, to) = currencyPair

        val aggregation = newAggregation(
            match(Criteria.where("payInCurrency").isEqualTo(from).and("payOutCurrency").isEqualTo(to)),

            lookup("rating", "pfiDID", "pfiDID", "pfiRatings"),

            addFields().addField("pfiRating").withValue(
                ConditionalOperators.ifNull(
                    ArrayOperators.arrayOf("pfiRatings").reduce(
                        ArithmeticOperators.valueOf("pfiRatings.rating").avg()
                    ).startingWith(0)
                ).then(0)
            ).build(),

            sort(Sort.Direction.DESC, "pfiRating").and(Sort.Direction.ASC, "rate"),

            project(
                "_id", "pfiName", "rate", "payInMethods", "payOutMethods",
                "payInCurrency", "payOutCurrency", "ref", "pfiRating"
            )
        )

        return mongoTemplate.aggregate(aggregation, "offering", OfferingDTO::class.java).mappedResults
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