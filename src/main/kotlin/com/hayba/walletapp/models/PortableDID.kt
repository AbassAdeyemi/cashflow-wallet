package com.hayba.walletapp.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import web5.sdk.crypto.jwk.Jwk
import web5.sdk.dids.did.PortableDid
import web5.sdk.dids.didcore.DidDocument

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PortableDID(
        @JsonProperty("uri")
        val uri: String,

        @JsonProperty("privateKeys")
        val privateKeys: List<PrivateKey>,

        @JsonProperty("document")
        val document: Document,

        @JsonProperty("metadata")
        val metadata: Map<String, Any>
)

data class PrivateKey(
        @JsonProperty("kty")
        val kty: String,

        @JsonProperty("crv")
        val crv: String,

        @JsonProperty("use")
        val use: String?,

        @JsonProperty("alg")
        val alg: String?,

        @JsonProperty("kid")
        val kid: String?,

        @JsonProperty("d")
        val d: String?,

        @JsonProperty("x")
        val x: String?,

        @JsonProperty("y")
        val y: String?
)

data class Document(
        @JsonProperty("id")
        val id: String,

        @JsonProperty("@context")
        val context: List<String>?,

        @JsonProperty("alsoKnownAs")
        val alsoKnownAs: List<String>?,

        @JsonProperty("controller")
        val controller: List<String>?,

        @JsonProperty("verificationMethod")
        val verificationMethod: List<VerificationMethod>?,

        @JsonProperty("service")
        val service: List<Service>?,

        @JsonProperty("assertionMethod")
        val assertionMethod: List<String>?,

        @JsonProperty("authentication")
        val authentication: List<String>?,

        @JsonProperty("keyAgreement")
        val keyAgreement: List<String>?,

        @JsonProperty("capabilityDelegation")
        val capabilityDelegation: List<String>?,

        @JsonProperty("capabilityInvocation")
        val capabilityInvocation: List<String>?
)

data class VerificationMethod(
        @JsonProperty("id")
        val id: String,

        @JsonProperty("type")
        val type: String,

        @JsonProperty("controller")
        val controller: String,

        @JsonProperty("publicKeyJwk")
        val publicKeyJwk: PublicKeyJwk
)

data class PublicKeyJwk(
        @JsonProperty("kty")
        val kty: String,

        @JsonProperty("crv")
        val crv: String,

        @JsonProperty("use")
        val use: String?,

        @JsonProperty("alg")
        val alg: String?,

        @JsonProperty("kid")
        val kid: String?,

        @JsonProperty("d")
        val d: String?,

        @JsonProperty("x")
        val x: String?,

        @JsonProperty("y")
        val y: String?
)

data class Service(
        @JsonProperty("id")
        val id: String,

        @JsonProperty("type")
        val type: String,

        @JsonProperty("serviceEndpoint")
        val serviceEndpoint: List<String>
)

object PortableDIDMapper {

    fun mapPortableDID(source: PortableDID): PortableDid {
        return PortableDid(
                uri = source.uri,
                privateKeys = source.privateKeys.map { mapPrivateKey(it) },
                document = mapDocument(source.document),
                metadata = source.metadata
        )
    }

    private fun mapPrivateKey(source: PrivateKey): Jwk {
        return Jwk(
                kty = source.kty,
                crv = source.crv,
                use = source.use,
                alg = source.alg,
                kid = source.kid,
                d = source.d,
                x = source.x,
                y = source.y
        )
    }

    private fun mapDocument(source: Document): DidDocument {
        return DidDocument(
                id = source.id,
                context = source.context,
                alsoKnownAs = source.alsoKnownAs,
                controller = source.controller,
                verificationMethod = source.verificationMethod?.map { mapVerificationMethod(it) },
                service = source.service?.map { mapService(it) },
                assertionMethod = source.assertionMethod?.map { it },
                authentication = source.authentication?.map { it },
                keyAgreement = source.keyAgreement?.map { it },
                capabilityDelegation = source.capabilityDelegation?.map { it },
                capabilityInvocation = source.capabilityInvocation?.map { it }
        )
    }

    private fun mapVerificationMethod(source: VerificationMethod): web5.sdk.dids.didcore.VerificationMethod {
        return web5.sdk.dids.didcore.VerificationMethod(
                id = source.id,
                type = source.type,
                controller = source.controller,
                publicKeyJwk = mapPublicKeyJwk(source.publicKeyJwk)
        )
    }

    private fun mapPublicKeyJwk(source: PublicKeyJwk): Jwk {
        return Jwk(
                kty = source.kty,
                crv = source.crv,
                use = source.use,
                alg = source.alg,
                kid = source.kid,
                d = source.d,
                x = source.x,
                y = source.y
        )
    }

    private fun mapService(source: Service): web5.sdk.dids.didcore.Service {
        return web5.sdk.dids.didcore.Service(
                id = source.id,
                type = source.type,
                serviceEndpoint = source.serviceEndpoint
        )
    }
}