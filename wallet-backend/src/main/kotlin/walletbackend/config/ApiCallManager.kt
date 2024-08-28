package walletbackend.config

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import java.io.IOException

object ApiCallManager {

    private val log = LoggerFactory.getLogger(ApiCallManager::class.java)

    // TODO: Should use a queuing system for retries for better scalability. SQS or Redis is fine.
    suspend fun <T> retryableApiCall(
            maxRetries: Int = 3,
            initialDelay: Long = 1000L,
            backOff: Double = 2.0,
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