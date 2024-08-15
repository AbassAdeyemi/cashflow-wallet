package walletbackend.services

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import walletbackend.models.OfferingDTO
import walletbackend.models.OrderCurrencyPair
import walletbackend.repositories.OfferingRepository
import walletbackend.repositories.PfiRepository

@Service
class AppService(private val pfiRepository: PfiRepository, private val offeringRepository: OfferingRepository) {
    companion object {
        private val log = LoggerFactory.getLogger(AppService::class.java)
    }

    fun getAvailablePairs(): List<OrderCurrencyPair> {
        return pfiRepository.findAll().flatMap { it.offerings }
                .map { OrderCurrencyPair(fromCurrency = it.fromCurrency, toCurrency = it.toCurrency) }
    }

    fun getMatchingOfferings(currencyPair: OrderCurrencyPair): List<OfferingDTO> {
        val (from, to) = currencyPair
        val offerings = offeringRepository.findByPayInCurrencyAndPayOutCurrency(from, to)

        return offerings.map {
            OfferingDTO(
                    pfiDID = it.pfiDID,
                    pfiName = it.pfiName,
                    rate = it.rate,
                    payInMethod = it.payInMethod,
                    payOutMethod = it.payOutMethod,
                    payInCurrency = it.payInCurrency,
                    payOutCurrency = it.payOutCurrency,
                    offeringID = it.offeringID
            )
        }
    }


}