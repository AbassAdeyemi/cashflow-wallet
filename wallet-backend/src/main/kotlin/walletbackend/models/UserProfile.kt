package walletbackend.models

data class UserProfile(
        val didUri: String,
        val credentials: List<Credential>
)


data class Credential (
        val issuer: String,
        val subject: String,
        val type: String
        )
