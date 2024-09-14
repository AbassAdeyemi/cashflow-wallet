package com.hayba.cashflow.services

import com.hayba.cashflow.entities.Rating
import com.hayba.cashflow.models.RatingRequest
import com.hayba.cashflow.repositories.RatingRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class RatingService(private val ratingRepository: RatingRepository) {

    fun ratePfi(ratingRequest: RatingRequest) {
        ratingRepository.save(
            Rating(
                id = UUID.randomUUID(),
                customerDID = ratingRequest.customerDID,
                pfiDID = ratingRequest.pfiDID,
                exchangeId = ratingRequest.exchangeId,
                rating = ratingRequest.rating
            )
        )
    }
}