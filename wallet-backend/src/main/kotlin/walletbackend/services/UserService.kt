package walletbackend.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import walletbackend.controllers.UserController
import walletbackend.entities.SignedVc
import walletbackend.models.Credential
import walletbackend.models.UserProfile
import walletbackend.repositories.SignedVcRepository
import web5.sdk.credentials.VerifiableCredential
import web5.sdk.crypto.KeyManager
import web5.sdk.dids.did.PortableDid
import web5.sdk.dids.methods.dht.DidDht
import java.util.*

@Service
class UserService(
        val signedVcRepository: SignedVcRepository,
        val keyManager: KeyManager,
        @Value("\${mock.credentials.url}")
        val credentialsBaseUrl: String
        ) {

    companion object {
        private val log = LoggerFactory.getLogger(UserController::class.java)
        private val restTemplate = RestTemplate()
    }

    fun authenticate(portableDid: PortableDid) {
        DidDht.import(portableDid, keyManager)
    }

    fun getUserProfile(didUri: String): UserProfile {
        val vcs = signedVcRepository.findAllByDidUri(didUri)
        val credentials = vcs.map { VerifiableCredential.parseJwt(it.signedJWT) }
                .map {Credential(issuer = it.issuer, subject = it.subject, type = it.type)}
        return UserProfile(didUri, credentials)
    }

    fun obtainVerifiableCredential(credentialRequirement: MockCredentialRequirement) {
        val (customerName, countryCode, customerDID) = credentialRequirement
        val fullUrl = "$credentialsBaseUrl?name=${customerName}&country=${countryCode}&did=${customerDID}"
        val response = restTemplate.exchange(fullUrl, HttpMethod.GET, null, String::class.java)
        if (response.statusCode.is2xxSuccessful && response.hasBody()) {
            val signedCredential = SignedVc(id = UUID.randomUUID(), didUri = customerDID, issuerName = "", signedJWT = response.body!!)
            signedVcRepository.save(signedCredential)
        }
    }

}


data class MockCredentialRequirement(
        val customerName: String,
        val countryCode: String,
        val customerDID: String
)

data class PaymentRequirement(val paymentFields: List<PaymentField>)

data class PaymentField(val fieldName: String, val required: Boolean)
