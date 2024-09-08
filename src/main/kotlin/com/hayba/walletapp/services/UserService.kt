package com.hayba.walletapp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.hayba.walletapp.config.CryptoUtil
import com.hayba.walletapp.controllers.UserController
import com.hayba.walletapp.entities.SignedVc
import com.hayba.walletapp.entities.UserDID
import com.hayba.walletapp.exceptions.InvalidFileException
import com.hayba.walletapp.exceptions.UserNotFoundException
import com.hayba.walletapp.models.Credential
import com.hayba.walletapp.models.MockCredentialRequirement
import com.hayba.walletapp.models.PortableDID
import com.hayba.walletapp.models.UserProfile
import com.hayba.walletapp.repositories.SignedVcRepository
import com.hayba.walletapp.repositories.UserDIDRepository
import io.ktor.server.util.*
import kotlinx.serialization.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import web5.sdk.credentials.VerifiableCredential
import web5.sdk.crypto.KeyManager
import web5.sdk.dids.methods.dht.CreateDidDhtOptions
import web5.sdk.dids.methods.dht.DidDht
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class UserService(
        val signedVcRepository: SignedVcRepository,
        val keyManager: KeyManager,
        val objectMapper: ObjectMapper,
        val userDIDRepository: UserDIDRepository,
        @Value("\${mock.credentials.url}")
        val mockCredentialBaseUrl: String,
        @Value("\${mock.credential.issuer-name}")
        val mockCredentialIssuer: String,
        @Value("\${did.encryption.secret}")
        val secret: String,
        val retryTemplate: RetryTemplate
) {

    private val log = LoggerFactory.getLogger(UserController::class.java)
    private val restTemplate = RestTemplate()

    fun register(): UserProfile {
        val bearerDid = DidDht.create(keyManager = keyManager, CreateDidDhtOptions(publish = true))
        val portableDID = objectMapper.convertValue(bearerDid.export(), PortableDID::class.java)
        val encryptedDID = CryptoUtil.encrypt(objectMapper.writeValueAsString(portableDID), secret)
        userDIDRepository.save(UserDID(id = UUID.randomUUID(), didUri = portableDID.uri, encryptedDID = encryptedDID))
        return UserProfile(didUri = portableDID.uri, credentials = emptyList())
    }

    fun getUserProfile(didUri: String): UserProfile {
        val optionalUserDID = userDIDRepository.findByDidUri(didUri)
        if (optionalUserDID.isEmpty) {
            throw UserNotFoundException("User with didUri: $didUri not found")
        }
        val credentials = mapCredentials(signedVcRepository.findAllByDidUri(didUri))
        return UserProfile(didUri = didUri, credentials = credentials)
    }

    fun obtainVerifiableCredential(credentialRequirement: MockCredentialRequirement): UserProfile {
        val (customerName, countryCode, customerDID) = credentialRequirement
        val fullUrl = "$mockCredentialBaseUrl?name=${customerName}&country=${countryCode}&did=${customerDID}"
        val credential = callGetCredentialApi(fullUrl)
        val signedCredential = SignedVc(id = UUID.randomUUID(), didUri = customerDID, issuerName = mockCredentialIssuer, signedJWT = credential)
        signedVcRepository.save(signedCredential)
        val credentials = mapCredentials(signedVcRepository.findAllByDidUri(customerDID))
        return UserProfile(customerDID, credentials)
    }

    fun downloadPortableDID(didUri: String): String {
        val optionalUserDid = userDIDRepository.findByDidUri(didUri)
        if (optionalUserDid.isEmpty) {
            throw UserNotFoundException("User with didUri: $didUri not found")
        }
        val userDid = optionalUserDid.get()
        val decrypted = CryptoUtil.decrypt(userDid.encryptedDID, secret)
        val jsonNode = objectMapper.readTree(decrypted)

        return objectMapper.writeValueAsString(jsonNode)
    }

    fun uploadPortableDID(file: MultipartFile): UserProfile {
        val portableDID: PortableDID?
        try {
            val response = objectMapper.readValue(file.inputStream, PortableDID::class.java)
            portableDID = response
        } catch (e: Exception) {
            log.error("Error occurred while reading file: {}", e.message)
            throw InvalidFileException("File is invalid, Please, upload a valid portable did file")
        }
        val optionalDid = userDIDRepository.findByDidUri(didUri = portableDID.uri)
        if (optionalDid.isPresent) {
            log.info("Portable did with didUri: {} exists", portableDID.uri)
            val credentials = mapCredentials(signedVcRepository.findAllByDidUri(portableDID.uri))
            return UserProfile(portableDID.uri, credentials)
        }
        val encryptedDid = CryptoUtil.encrypt(objectMapper.writeValueAsString(portableDID), secret)
        userDIDRepository.save(UserDID(id = UUID.randomUUID(), didUri = portableDID.uri, encryptedDID = encryptedDid))
        val credentials = mapCredentials(signedVcRepository.findAllByDidUri(portableDID.uri))
        return UserProfile(portableDID.uri, credentials)
    }

    private fun callGetCredentialApi(url: String): String {
        return retryTemplate.execute<String, RuntimeException> {
            restTemplate.getForObject(url, String::class.java)
        }
    }

    private fun mapCredentials(vcs: List<SignedVc>): List<Credential> {
        return vcs
                .map { vc ->
                    val parsedVc = VerifiableCredential.parseJwt(vc.signedJWT)
                    Credential(
                            issuer = vc.issuerName,
                            subject = parsedVc.subject,
                            type = parsedVc.type,
                            issuanceDate = LocalDateTime.from(parsedVc.vcDataModel.issuanceDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()),
                            expirationDate = LocalDateTime.from(parsedVc.vcDataModel.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    )
                }
    }
}
