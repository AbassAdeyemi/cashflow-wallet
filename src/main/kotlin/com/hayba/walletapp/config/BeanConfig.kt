package com.hayba.walletapp.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.retry.RetryPolicy
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import web5.sdk.crypto.InMemoryKeyManager
import web5.sdk.crypto.KeyManager


@Component
class BeanConfig {

    @Bean
    fun keyManager(): KeyManager {
        return InMemoryKeyManager()
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)  // Enable pretty-print
        }.registerKotlinModule()
                .registerModule(JavaTimeModule())
        return objectMapper
    }

//    @Bean
//    fun retryTemplate(): RetryTemplate {
//        val retryTemplate = RetryTemplate()
//        val fixedBackOffPolicy = FixedBackOffPolicy()
//        fixedBackOffPolicy.backOffPeriod = 3000L
//        retryTemplate.setBackOffPolicy(fixedBackOffPolicy)
//        val retryPolicy = SimpleRetryPolicy()
//        retryPolicy.maxAttempts = 2
//        retryTemplate.setRetryPolicy(retryPolicy)
//        return retryTemplate
//    }

    @Bean
    fun retryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()

        // Retry Policy: Max Attempts
        val retryPolicy: RetryPolicy = SimpleRetryPolicy(3)
        retryTemplate.setRetryPolicy(retryPolicy)

        // Backoff Policy: Exponential with Jitter
        val backOffPolicy = ExponentialBackOffPolicy().apply {
            initialInterval = 1000L  // Initial delay (1 second)
            maxInterval = 10000L     // Maximum delay (10 seconds)
            multiplier = 2.0         // Exponential multiplier
        }
        retryTemplate.setBackOffPolicy(backOffPolicy)

        return retryTemplate
    }
}