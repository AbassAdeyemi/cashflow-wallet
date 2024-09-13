package com.hayba.walletapp.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.hayba.walletapp.entities.Offering
import com.hayba.walletapp.entities.PaymentField
import com.hayba.walletapp.entities.PaymentMethod
import com.hayba.walletapp.entities.Pfi
import com.hayba.walletapp.repositories.OfferingRepository
import com.hayba.walletapp.repositories.PfiRepository
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.retry.RecoveryCallback
import org.springframework.retry.RetryCallback
import org.springframework.retry.support.RetryTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import tbdex.sdk.httpclient.TbdexHttpClient
import java.math.BigDecimal
import java.util.*

@Component
class DBInitializer(private val pfiRepository: PfiRepository,
                    private val objectMapper: ObjectMapper,
                    private val offeringRepository: OfferingRepository,
                    private val retryTemplate: RetryTemplate,
                    private val mongoTemplate: MongoTemplate) {

    private val initializerScope = CoroutineScope(Dispatchers.IO)
    private val log = LoggerFactory.getLogger(DBInitializer::class.java)

    @PostConstruct
    fun initializeDB() {
        try {
            loadPfisToDB()
        } catch (e: Exception) {
            log.error("Migration Failed: {}", e.message)
        }
        val pfis = pfiRepository.findAll()
        val offeringRefs = offeringRepository.findAllByStaleIsFalse().map { it.ref }.toSet()
        initializerScope.launch {
            loadOfferingsFromPfis(pfis, offeringRefs)
        }
    }

    private fun loadPfisToDB() {
        val pfis = convertJsonToPfis("pfi.json")
        val savedPfis = pfiRepository.saveAll(pfis)
        log.info("Loaded {} pfis successfully", savedPfis.count())
    }

    private fun convertJsonToPfis(fileName: String): List<Pfi> {
        val file = ResourceUtils.getFile("classpath:$fileName")
        val typeRef = object : TypeReference<ArrayList<Pfi>>() {}
        return objectMapper.readValue(file.readText(), typeRef)
    }

    private suspend fun loadOfferingsFromPfis(pfis: List<Pfi>, offeringRefs: Set<String>) = coroutineScope {
        val didNameMap = pfis.associate { it.didUri to it.name }
        val result = pfis.map { pfi ->
            async {
                retryTemplate.execute(
                        RetryCallback<List<tbdex.sdk.protocol.models.Offering>, RuntimeException> {
                            TbdexHttpClient.getOfferings(pfiDid = pfi.didUri)
                        },
                        RecoveryCallback {
                            log.error("Error fetching offerings: {}", it.lastThrowable?.message)
                            emptyList()
                        }
                )
            }
        }

        val externalOfferings = result.awaitAll().flatten()

        if(externalOfferings.isEmpty())  {
            log.error("Could not get any offering from pfis, Please check internet connectivity or pfis availability")
            return@coroutineScope
        }

        val staleOfferingRefs = getStaleOfferingRefs(
                offeringRefs = offeringRefs,
                externalOfferingRefs = externalOfferings.map { it.metadata.id }.toSet()
        )

        val offeringEntities = mutableListOf<Offering>()
        for (externalOffering in externalOfferings) {
            if (offeringRefs.contains(externalOffering.metadata.id)) continue; //Do not attempt saving existing offering
            val offeringEntity = Offering(
                    id = UUID.randomUUID(),
                    pfiDID = externalOffering.metadata.from,
                    pfiName = didNameMap.getValue(externalOffering.metadata.from),
                    rate = BigDecimal(externalOffering.data.payoutUnitsPerPayinUnit),
                    payInMethods = externalOffering.data.payin.methods.map { mapTbdexPaymentMethodToPaymentMethod(it) },
                    payOutMethods = externalOffering.data.payout.methods.map { mapTbdexPaymentMethodToPaymentMethod(it) },
                    payInCurrency = externalOffering.data.payin.currencyCode,
                    payOutCurrency = externalOffering.data.payout.currencyCode,
                    ref = externalOffering.metadata.id,
                    jsonString = objectMapper.writeValueAsString(externalOffering)
            )
            offeringEntities.add(offeringEntity)
        }
        offeringRepository.saveAll(offeringEntities)
        log.info("Loaded {} new offerings into db", offeringEntities.count())

        markOfferingAsStale(staleOfferingRefs)
        log.info("Marked {} offerings as stale", staleOfferingRefs.count())
    }

    private fun mapTbdexPaymentMethodToPaymentMethod(tbdexPaymentMethod: tbdex.sdk.protocol.models.PaymentMethod): PaymentMethod {
        return PaymentMethod(
                kind = tbdexPaymentMethod.kind,
                paymentFields = tbdexPaymentMethod.requiredPaymentDetails?.let { extractPaymentFields(it) }
                        ?: emptyList()
        )
    }

    private fun extractPaymentFields(jsonNode: JsonNode): List<PaymentField> {
        val requiredFields = jsonNode["required"]?.map { it.asText() }?.toSet() ?: emptySet()
        val propertiesNode = jsonNode["properties"]
        val paymentFields = mutableListOf<PaymentField>()

        propertiesNode?.fieldNames()?.forEach { fieldName ->
            val isRequired = requiredFields.contains(fieldName)
            paymentFields.add(PaymentField(fieldName, isRequired))
        }

        return paymentFields
    }

    private fun getStaleOfferingRefs(offeringRefs: Set<String>, externalOfferingRefs: Set<String>): Set<String> {
        return offeringRefs.subtract(externalOfferingRefs)
    }

    private fun markOfferingAsStale(refs: Set<String>) {
        val query = Query(Criteria.where("ref").`in`(refs))
        val update: Update = Update().set("stale", true)
        mongoTemplate.updateMulti(query, update, Offering::class.java)
    }

    @Scheduled(cron = "0 0 * * * *")
    fun refreshOfferings() {
        log.info("Refreshing offerings")
        val pfis = pfiRepository.findAll()
        val offeringRefs = offeringRepository.findAllByStaleIsFalse().map { it.ref }.toSet()
        initializerScope.launch {
            loadOfferingsFromPfis(pfis, offeringRefs)
        }
    }
}