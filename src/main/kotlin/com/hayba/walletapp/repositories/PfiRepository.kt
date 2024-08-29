package com.hayba.walletapp.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import com.hayba.walletapp.entities.Pfi
import java.util.*

interface PfiRepository: MongoRepository<Pfi, UUID> {
}