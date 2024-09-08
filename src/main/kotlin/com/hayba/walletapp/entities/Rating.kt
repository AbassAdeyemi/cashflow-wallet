package com.hayba.walletapp.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document
data class Rating(
        @Id
        val id: UUID,
        val customerDID: String,
        val pfiDID: String,
        val exchangeId: String,
        val rating: Int
)
