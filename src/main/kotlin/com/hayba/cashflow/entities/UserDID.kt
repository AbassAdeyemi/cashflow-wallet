package com.hayba.cashflow.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class UserDID(
        @Id
        val id: UUID,
        @Indexed(unique = true)
        val didUri: String,
        val encryptedDID: String
)
