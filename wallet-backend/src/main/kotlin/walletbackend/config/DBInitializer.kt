package walletbackend.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import tbdex.sdk.httpclient.TbdexHttpClient
import walletbackend.entities.OfferingEntity
import walletbackend.entities.Pfi
import walletbackend.models.PaymentField
import walletbackend.models.PaymentMethod
import walletbackend.repositories.OfferingRepository
import walletbackend.repositories.PfiRepository
import java.io.IOException
import java.math.BigDecimal
import java.util.*

@Component
class DBInitializer(private val pfiRepository: PfiRepository, private val objectMapper: ObjectMapper,
                    private val offeringRepository: OfferingRepository) {

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    private val log = LoggerFactory.getLogger(DBInitializer::class.java)

    @PostConstruct
    fun initializeDB() {
        try {
            pfiRepository.deleteAll()
            loadPfisToDB()
        } catch (e: Exception) {
            log.error("Migration Failed: {}", e.message)
        }
            offeringRepository.deleteAll()
            applicationScope.launch {
                loadOfferingsFromPfis()
            }
    }

    private fun loadPfisToDB(){
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
            async { retryableApiCall { TbdexHttpClient.getOfferings(pfiDid = pfi.didUri) } ?: emptyList() }
        }
        val offerings = result.awaitAll().flatten()
        val offeringEntities = offerings.map { offering ->
            OfferingEntity(
                    id = UUID.randomUUID(),
                    pfiDID = offering.metadata.from,
                    pfiName = didNameMap.getValue(offering.metadata.from),
                    rate = BigDecimal(offering.data.payoutUnitsPerPayinUnit),
                    payInMethod = offering.data.payin.methods.
                    map { mapTbdexPaymentMethodToPaymentMethod(it)},
                    payOutMethod = offering.data.payout.methods.
                    map { mapTbdexPaymentMethodToPaymentMethod(it)},
                    payInCurrency = offering.data.payin.currencyCode,
                    payOutCurrency = offering.data.payout.currencyCode,
                    offeringID = offering.metadata.id
            )
        }
        offeringRepository.saveAll(offeringEntities)
        log.info("Loaded {} offerings successfully into db", offeringEntities.count())
    }

    private fun mapTbdexPaymentMethodToPaymentMethod(tbdexPaymentMethod: tbdex.sdk.protocol.models.PaymentMethod): PaymentMethod {
        return PaymentMethod(
                kind = tbdexPaymentMethod.kind,
                paymentFields = tbdexPaymentMethod.requiredPaymentDetails?.
                let { extractPaymentFields(it) } ?: emptyList()
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



    suspend fun <T> retryableApiCall(
            maxRetries: Int = 3,
            initialDelay: Long = 1000L,
            backOff: Double = 4.0,
            apiCall: suspend () -> T
    ): T? {
        var currentDelay = initialDelay
        repeat(maxRetries) { attempt ->
            try {
                return apiCall()
            } catch (e: IOException) {
                if (attempt == maxRetries - 1) {
                    log.error("Error occurred: {}", e.message)
                    log.error("Max retries reached.")
                  return null
                }
                delay(currentDelay)
                currentDelay = (currentDelay * backOff).toLong()
            }
        }
        return null
    }

}