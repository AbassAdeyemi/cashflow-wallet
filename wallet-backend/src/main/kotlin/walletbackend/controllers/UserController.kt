package walletbackend.controllers

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import web5.sdk.crypto.InMemoryKeyManager

@RestController
@RequestMapping("/users")
internal class UserController(private val inMemoryKeyManager: InMemoryKeyManager) {

    companion object {
        private val log = LoggerFactory.getLogger(UserController::class.java)
    }
}