package walletbackend.controllers

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import walletbackend.services.UserService
import web5.sdk.credentials.VerifiableCredential
import web5.sdk.crypto.InMemoryKeyManager
import web5.sdk.dids.did.PortableDid
import web5.sdk.dids.methods.dht.DidDht

@RestController
@RequestMapping("/users")
internal class UserController(private val userService: UserService) {
    @PostMapping("/profile")
    fun authenticateUser(portableDid: PortableDid): ResponseEntity<Void> {
        userService.authenticate(portableDid)
        return ResponseEntity.ok().build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserController::class.java)
    }
}