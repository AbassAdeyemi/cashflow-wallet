package com.hayba.walletapp.controllers

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import com.hayba.walletapp.models.MockCredentialRequirement
import com.hayba.walletapp.models.UserProfile
import com.hayba.walletapp.services.UserService
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/users")
internal class UserController(private val userService: UserService) {

    private val log = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/register")
    fun register(): ResponseEntity<UserProfile> {
        val userProfile = userService.register()
        return ResponseEntity(userProfile, HttpStatus.CREATED)
    }

    @GetMapping("/{didUri}/profile")
    fun getProfile(@PathVariable(name = "didUri") didUri: String): ResponseEntity<UserProfile> {
        val userProfile = userService.getUserProfile(didUri)
        return ResponseEntity.ok(userProfile)
    }
    @GetMapping("/{didUri}/download")
    fun downloadPortableDID(@PathVariable(name = "didUri") didUri: String): ResponseEntity<ByteArray> {
        val stringifiedPortableDID = userService.downloadPortableDID(didUri = didUri)
        val headers = HttpHeaders().apply {
            contentType = MediaType.TEXT_PLAIN
            setContentDispositionFormData("attachment", "portable_did.json")
        }
         return ResponseEntity(
                stringifiedPortableDID.toByteArray(StandardCharsets.UTF_8),
                headers,
                HttpStatus.OK
        )
    }

    @PostMapping("/upload")
    fun uploadPortableDID(@RequestParam("file")file: MultipartFile): ResponseEntity<UserProfile> {
        val userProfile = userService.uploadPortableDID(file)
        return ResponseEntity.ok(userProfile)
    }

    @PostMapping("/credentials")
    fun obtainVerifiableCredential(@RequestBody mockCredentialRequirement: MockCredentialRequirement): ResponseEntity<UserProfile> {
        return ResponseEntity(userService.obtainVerifiableCredential(mockCredentialRequirement), HttpStatus.CREATED)
    }
}