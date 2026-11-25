package com.quan.finance_app.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TokenCipherTest {

    private val cipher = TokenCipher()

    @Test
    fun encryptThenDecrypt_returnsOriginalPlaintext() {
        val raw = "k3J9_sample-refresh-token-256bit_AbCdEfGhIjKlMnOpQrStUvWxYz"
        val encrypted = cipher.encrypt(raw)

        assertNotEquals(raw, encrypted)
        assertEquals(raw, cipher.decrypt(encrypted))
    }

    @Test
    fun encryptSamePlaintextTwice_producesDifferentCiphertexts() {
        val raw = "same-refresh-token-value"
        val enc1 = cipher.encrypt(raw)
        val enc2 = cipher.encrypt(raw)

        assertNotEquals(enc1, enc2)
        assertEquals(raw, cipher.decrypt(enc1))
        assertEquals(raw, cipher.decrypt(enc2))
    }

    @Test
    fun decryptTamperedCiphertext_returnsNull() {
        val raw = "tamper-check-refresh-token"
        val encrypted = cipher.encrypt(raw)
        assertNotNull(encrypted)

        val mid = encrypted.length / 2
        val replacement = if (encrypted[mid] == 'A') 'B' else 'A'
        val tampered = encrypted.substring(0, mid) + replacement + encrypted.substring(mid + 1)

        assertNull(cipher.decrypt(tampered))
    }

    @Test
    fun decryptGarbageOrTooShort_returnsNull() {
        assertNull(cipher.decrypt(""))
        assertNull(cipher.decrypt("not-valid-base64!!!@@@"))
        assertNull(cipher.decrypt("YWJjZA=="))
    }
}
