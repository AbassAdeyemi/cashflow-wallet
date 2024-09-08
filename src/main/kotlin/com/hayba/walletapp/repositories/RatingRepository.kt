package com.hayba.walletapp.repositories

import com.hayba.walletapp.entities.Rating
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface RatingRepository: MongoRepository<Rating, UUID> {
}