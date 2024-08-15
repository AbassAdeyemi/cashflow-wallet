package walletbackend.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
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
        return ObjectMapper()
    }
}