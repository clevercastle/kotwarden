package org.clevercastle.kotwarden.util

import org.bouncycastle.util.encoders.Hex
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

const val PBKDF_ALGORITHM = "PBKDF2WithHmacSHA256"
const val KEY_LENGTH = 32 * 8

fun hashPassword(secret: String, salt: ByteArray, iterations: Int): String {
    val pbeKeySpec = PBEKeySpec(secret.toCharArray(), salt, iterations, KEY_LENGTH)
    val skf = SecretKeyFactory.getInstance(PBKDF_ALGORITHM)
    return Hex.toHexString(skf.generateSecret(pbeKeySpec).encoded)
}

fun verifyPassword(secret: String, salt: ByteArray, passwordHash: String, iterations: Int): Boolean {
    val hashedPassword = hashPassword(secret, salt, iterations)
    return hashedPassword == passwordHash
}