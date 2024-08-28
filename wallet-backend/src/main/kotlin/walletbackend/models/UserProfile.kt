package walletbackend.models

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class UserProfile(
        val didUri: String,
        val credentials: List<Credential>
)


data class Credential (
        val issuer: String,
        val subject: String,
        val type: String,
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        val issuanceDate: LocalDateTime,
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        val expirationDate: LocalDateTime
        )
