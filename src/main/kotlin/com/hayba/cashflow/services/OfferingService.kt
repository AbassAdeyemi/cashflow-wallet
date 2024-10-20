package com.hayba.cashflow.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.hayba.cashflow.exceptions.OfferingNotFoundException
import com.hayba.cashflow.models.OfferingDTO
import com.hayba.cashflow.models.OrderCurrencyPair
import com.hayba.cashflow.repositories.OfferingRepository
import com.hayba.cashflow.repositories.PfiRepository
import com.hayba.cashflow.repositories.SignedVcRepository
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
        return pfiRepository.findAll().asSequence().flatMap { it.offeringCurrencyPairs }
            .map { OrderCurrencyPair(fromCurrency = it.fromCurrency, toCurrency = it.toCurrency) }.distinct().toList()
    }

    fun getMatchingOfferings(currencyPair: OrderCurrencyPair): List<OfferingDTO> {
        val (from, to) = currencyPair

        val aggregation = newAggregation(
            match(Criteria.where("payInCurrency").isEqualTo(from).and("payOutCurrency").isEqualTo(to)),

            lookup("rating", "pfiDID", "pfiDID", "pfiRatings"),

            addFields().addField("pfiRating").withValue(
                ConditionalOperators.ifNull(
                    ArithmeticOperators.Round.roundValueOf(
                    ArrayOperators.arrayOf("pfiRatings").reduce(
                        ArithmeticOperators.valueOf("pfiRatings.rating").avg()
                    ).startingWith(0)
                ).place(1)).then(0)
            ).build(),

            sort(Sort.Direction.DESC, "pfiRating").and(Sort.Direction.ASC, "rate"),

            project(
                "_id", "pfiName", "pfiDID", "rate", "payInMethods", "payOutMethods",
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