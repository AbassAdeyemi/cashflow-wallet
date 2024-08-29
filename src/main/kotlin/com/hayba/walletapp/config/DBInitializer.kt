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
import com.hayba.walletapp.services.ExchangeService
import com.hayba.walletapp.services.UserService
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import tbdex.sdk.httpclient.TbdexHttpClient
import web5.sdk.crypto.KeyManager
import java.math.BigDecimal
import java.util.*

@Component
class DBInitializer(private val pfiRepository: PfiRepository,
                    private val objectMapper: ObjectMapper,
                    private val offeringRepository: OfferingRepository,
                    private val userService: UserService,
                    private val exchangeService: ExchangeService,
                    private val keyManager: KeyManager) {

    private val initializerScope = CoroutineScope(Dispatchers.IO)
    private val log = LoggerFactory.getLogger(DBInitializer::class.java)

    @PostConstruct
    fun initializeDB() {
//        try {
//            loadPfisToDB()
//        } catch (e: Exception) {
//            log.error("Migration Failed: {}", e.message)
//        }
//        // TODO: This is a workaround for removing stale offerings.Should implement proper offering management
//        offeringRepository.deleteAll()
//        initializerScope.launch {
//            loadOfferingsFromPfis()
//        }
//        exchangeService.handleQuoteNextStep(QuoteNextStep(quoteId = "quote_01j60aty1qf7wtdx48zr2xw831", proceed = true))
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

    private suspend fun loadOfferingsFromPfis() = coroutineScope {
        val pfis = pfiRepository.findAll()
        val didNameMap = pfis.associate { it.didUri to it.name }
        val result = pfis.map { pfi ->
             async { ApiCallManager.retryableApiCall { TbdexHttpClient.getOfferings(pfiDid = pfi.didUri) } ?: emptyList() }
        }
        val offerings = result.awaitAll().flatten()
        val offeringEntities = offerings.map { offering ->
            val offeringString = objectMapper.writeValueAsString(offering)
            Offering(
                    id = UUID.randomUUID(),
                    pfiDID = offering.metadata.from,
                    pfiName = didNameMap.getValue(offering.metadata.from),
                    rate = BigDecimal(offering.data.payoutUnitsPerPayinUnit),
                    payInMethods = offering.data.payin.methods.map { mapTbdexPaymentMethodToPaymentMethod(it) },
                    payOutMethods = offering.data.payout.methods.map { mapTbdexPaymentMethodToPaymentMethod(it) },
                    payInCurrency = offering.data.payin.currencyCode,
                    payOutCurrency = offering.data.payout.currencyCode,
                    ref = offering.metadata.id,
                    jsonString = offeringString
            )
        }
        offeringRepository.saveAll(offeringEntities)
        log.info("Loaded {} offerings successfully into db", offeringEntities.count())
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
}