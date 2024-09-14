package com.hayba.cashflow.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = listOf("*")
        corsConfig.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        corsConfig.allowedHeaders = listOf("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)

        return CorsFilter(source)
    }
}