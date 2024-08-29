package com.hayba.walletapp.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "pfis")
data class Pfi(
        @Id
        @JsonProperty("id")
        val id: UUID,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("didUri")
        val didUri: String,
        @JsonProperty("offeringCurrencyPairs")
        val offeringCurrencyPairs: List<OfferingCurrencyPair>
)

data class OfferingCurrencyPair(
        @JsonProperty("fromCurrency")
        val fromCurrency: String,
        @JsonProperty("toCurrency")
        val toCurrency: String
        )
