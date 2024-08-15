package walletbackend.services

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import tbdex.sdk.httpclient.TbdexHttpClient
import tbdex.sdk.httpclient.models.GetOfferingsFilter
import walletbackend.config.DBInitializer
import walletbackend.models.OrderCurrencyPair
import walletbackend.repositories.PfiRepository

@Service
class ExchangeService(val pfiRepository: PfiRepository) {

        private val log = LoggerFactory.getLogger(ExchangeService::class.java)



}
