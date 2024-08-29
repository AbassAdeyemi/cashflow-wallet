package com.hayba.walletapp.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document
data class SignedVc(
        @Id
        val id: UUID,
        @Indexed
        val didUri: String,
        val issuerName: String,
        val signedJWT: String
        )

