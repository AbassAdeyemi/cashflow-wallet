package com.hayba.cashflow.controllers

import com.hayba.cashflow.models.RatingRequest
import com.hayba.cashflow.services.RatingService
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