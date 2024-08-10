package walletbackend.entities

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
        @JsonProperty("offerings")
        val offerings: MutableList<Offering>
)

data class Offering(
        @JsonProperty("fromCurrency")
        val fromCurrency: String,
        @JsonProperty("toCurrency")
        val toCurrency: String
        )
