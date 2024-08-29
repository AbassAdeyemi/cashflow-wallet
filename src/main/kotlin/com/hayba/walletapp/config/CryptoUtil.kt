package com.hayba.walletapp.config

import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {
    private val ALGORITHM = "AES"
    private val TRANSFORMATION = "AES"

    fun encrypt(input: String, secret: String): String {
        val secretKey = getSecretKey(secret)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encrypted: String, secret: String): String {
        val secretKey = getSecretKey(secret)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted))
        return String(decryptedBytes)
    }

    private fun getSecretKey(secret: String): SecretKey {
        val keyBytes = secret.toByteArray().copyOf(16) // Ensure key is 16 bytes (128 bits) for AES-128
        return SecretKeySpec(keyBytes, ALGORITHM)
    }
}