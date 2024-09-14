package com.hayba.cashflow.repositories

import com.hayba.cashflow.entities.Rating
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface RatingRepository: MongoRepository<Rating, UUID> {
}