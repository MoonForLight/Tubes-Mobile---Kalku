package com.example.kalku.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordUtilsTest {

    @Test
    fun hashAndVerify_validPassword_succeeds() {
        val hash = PasswordUtils.hash("Kalku123")
        assertNotEquals("Kalku123", hash)
        assertTrue(PasswordUtils.isHashed(hash))
        assertTrue(PasswordUtils.verify("Kalku123", hash))
        assertFalse(PasswordUtils.verify("Salah123", hash))
    }

    @Test
    fun verify_legacyPlainText_stillSupported() {
        assertTrue(PasswordUtils.verify("password8", "password8"))
        assertFalse(PasswordUtils.verify("password9", "password8"))
    }
}
