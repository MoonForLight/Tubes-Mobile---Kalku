package com.example.kalku.utils

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Penyimpanan password lokal menggunakan PBKDF2.
 * Format nilai: pbkdf2$iterations$saltHex$hashHex
 *
 * Password lama yang masih berupa teks biasa tetap dapat diverifikasi lalu
 * otomatis diubah menjadi hash ketika pengguna berhasil login.
 */
object PasswordUtils {
    private const val PREFIX = "pbkdf2"
    private const val ITERATIONS = 60_000
    private const val KEY_LENGTH = 256
    private const val SALT_BYTES = 16

    fun hash(password: String): String {
        require(password.isNotBlank()) { "Password tidak boleh kosong" }
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = derive(password, salt, ITERATIONS)
        return listOf(PREFIX, ITERATIONS.toString(), salt.toHex(), hash.toHex())
            .joinToString("\$")
    }

    fun verify(password: String, storedValue: String): Boolean {
        if (!isHashed(storedValue)) return password == storedValue

        val parts = storedValue.split('$')
        if (parts.size != 4) return false

        return runCatching {
            val iterations = parts[1].toInt()
            val salt = parts[2].hexToBytes()
            val expected = parts[3].hexToBytes()
            val actual = derive(password, salt, iterations)
            constantTimeEquals(expected, actual)
        }.getOrDefault(false)
    }

    fun isHashed(value: String): Boolean = value.startsWith("$PREFIX\$")

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun constantTimeEquals(left: ByteArray, right: ByteArray): Boolean {
        if (left.size != right.size) return false
        var result = 0
        for (index in left.indices) {
            result = result or (left[index].toInt() xor right[index].toInt())
        }
        return result == 0
    }

    private fun ByteArray.toHex(): String = joinToString("") { byte -> "%02x".format(byte.toInt() and 0xFF) }

    private fun String.hexToBytes(): ByteArray {
        require(length % 2 == 0)
        return ByteArray(length / 2) { index ->
            substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
    }
}
