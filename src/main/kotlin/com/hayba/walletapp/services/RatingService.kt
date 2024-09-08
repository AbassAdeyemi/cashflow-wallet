package com.hayba.walletapp.services

import com.hayba.walletapp.entities.Rating
import com.hayba.walletapp.models.RatingRequest
import com.hayba.walletapp.repositories.RatingRepository
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