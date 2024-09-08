package com.hayba.walletapp.controllers

import com.hayba.walletapp.models.RatingRequest
import com.hayba.walletapp.services.RatingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ratings")
class RatingController(private val ratingService: RatingService) {

    @PostMapping
    fun ratePfi(@RequestBody ratingRequest: RatingRequest): ResponseEntity<Void> {
        ratingService.ratePfi(ratingRequest)
        return ResponseEntity.ok().build()
    }
}