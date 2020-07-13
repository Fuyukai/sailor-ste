/*
 * This file is part of Sailor STE.
 *
 * Sailor STE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sailor STE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Sailor STE.  If not, see <https://www.gnu.org/licenses/>.
 */

package tf.sailor.sste.crypto

import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava
import com.goterl.lazycode.lazysodium.interfaces.*

/** Typealias used for message/nonce pairs. */
public typealias MessageNoncePair = Pair<ByteArray, ByteArray>

/**
 * Cryptography class. Used as a singleton and wraps libsodium.
 */
@Suppress("MemberVisibilityCanBePrivate")
public object Crypto {
    private val sodium = LazySodiumJava(SodiumJava(), Charsets.UTF_8)

    /**
     * Gets [size] random bytes from libsodium.
     */
    public fun randomBytes(size: Int): ByteArray = sodium.randomBytesBuf(size)

    // TODO: Maybe allow arbitrary keys?
    // This isn't unsafe without them, but it might be a useful feature.
    /**
     * Hashes a ByteArray [ba] with an **integrity** hash (i.e. not a password hash).
     *
     * This should be used for things like generating a checksum of a file, NOT for password
     * hashing or HMACs.
     */
    public fun integrityHash(ba: ByteArray): ByteArray {
        val out = ByteArray(GenericHash.BYTES_MAX)
        val success = sodium.cryptoGenericHash(
            out, GenericHash.BYTES_MAX,
            ba, ba.size.toLong(),
            null, 0
        )

        if (!success) {
            sodiumFailed("crypto_generichash")
        }

        return out
    }

    /**
     * Verifies a ByteArray [ba] against an **integrity** hash [hash]
     */
    public fun integrityHashVerify(ba: ByteArray, hash: ByteArray): Boolean {
        val baHash = integrityHash(ba)

        // reader note: always check each byte to avoid easily leaking timing data.
        // does this make things more secure? maybe not.
        // does it make things *less* secure? no.
        var isBad = false
        for (idx in baHash.indices) {
            val first = baHash[idx]
            val second = hash[idx]
            if (first != second) isBad = true
        }

        return !isBad
    }

    /**
     * Hashes a String [password] with a **password hash**.
     */
    public fun passwordHash(password: String): String {
        return passwordHash(password.toByteArray(Charsets.UTF_8))
    }

    /**
     * Hashes a ByteArray [pw] with a **password hash**. This is suitable for, well, storing
     * hashed passwords in a database or similar.
     */
    public fun passwordHash(pw: ByteArray): String {
        // STR_BYTES -> crypto_pwhash_STRBYTES
        val out = ByteArray(PwHash.STR_BYTES)
        val success = sodium.cryptoPwHashStr(
            out, pw, pw.size,
            PwHash.ARGON2ID_OPSLIMIT_INTERACTIVE,
            PwHash.MEMLIMIT_INTERACTIVE
        )

        if (!success) {
            sodiumFailed("crypto_pwhash_str")
        }

        return String(out, Charsets.US_ASCII)
    }

    /**
     * Verifies a password [hash] against the specified [password].
     */
    public fun passwordVerify(password: String, hash: String): Boolean {
        return passwordVerify(password.toByteArray(Charsets.UTF_8), hash)
    }

    /**
     * Verifies a password hash against the specified password [pw].
     */
    public fun passwordVerify(pw: ByteArray, hash: String): Boolean {
        var hashBa = hash.toByteArray(Charsets.US_ASCII)
        // ensure hash has null byte
        if (hashBa.last() != (0).toByte()) {
            val hashWithNullByte = ByteArray(hashBa.size + 1)
            System.arraycopy(hashBa, 0, hashWithNullByte, 0, hashBa.size)
            hashBa = hashWithNullByte
        }

        return (sodium as PwHash.Native).cryptoPwHashStrVerify(hashBa, pw, pw.size)
    }

    /**
     * Derives a symmetric encryption key from the specified input.
     */
    public fun symmetricKeyDerive(input: ByteArray): ByteArray {
        val output = ByteArray(SecretBox.KEYBYTES)
        val salt = randomBytes(PwHash.SALTBYTES)

        val success = (sodium as PwHash.Native).cryptoPwHash(
            output, output.size,
            input, input.size,
            salt,
            PwHash.OPSLIMIT_INTERACTIVE, PwHash.MEMLIMIT_INTERACTIVE,
            PwHash.Alg.getDefault()
        )

        if (!success) {
            sodiumFailed("crypto_pwhash")
        }

        return output
    }

    /**
     * Symmetrically encrypts a ByteArray [ba] using the key [key].
     *
     * __**The key must be a derived key as produced by [symmetricKeyDerive].**__
     */
    @Throws(SodiumException::class)
    public fun symmetricEncryptSingleMessage(
        ba: ByteArray, key: ByteArray, nonce: ByteArray
    ): ByteArray {
        lengthCheck(key.size == SecretBox.KEYBYTES, "key")
        lengthCheck(nonce.size == SecretBox.NONCEBYTES, "nonce")

        val out = ByteArray(ba.size + SecretBox.MACBYTES)
        val success = (sodium as SecretBox.Native).cryptoSecretBoxEasy(
            out,
            ba, ba.size.toLong(),
            nonce, key
        )

        if (!success) {
            sodiumFailed("crypto_secretbox_easy")
        }

        return out
    }

    /**
     * Symmetrically encrypts a ByteArray [ba] using the key [key].
     *
     * This will generate a nonce automatically, and return it in a pair with the ciphertext.
     */
    @Throws(SodiumException::class)
    public fun symmetricEncryptSingleMessage(ba: ByteArray, key: ByteArray): MessageNoncePair {
        val nonce = randomBytes(SecretBox.NONCEBYTES)
        val ciphertext = symmetricEncryptSingleMessage(ba, key, nonce)
        return MessageNoncePair(ciphertext, nonce)
    }

    /**
     * Symmetrically decrypts a ByteArray [ba] using the key [key] and the nonce [nonce].
     */
    @Throws(DecryptionFailedException::class)
    public fun symmetricDecryptSingleMessage(
        ba: ByteArray, key: ByteArray, nonce: ByteArray
    ): ByteArray {
        check(key.size == SecretBox.KEYBYTES) { "Key is incorrect length!" }
        check(nonce.size == SecretBox.NONCEBYTES) { "Nonce is incorrect length!" }

        // tricky parameters...
        val out = ByteArray(ba.size - SecretBox.MACBYTES)
        val success = (sodium as SecretBox.Native).cryptoSecretBoxOpenEasy(
            out, ba, ba.size.toLong(),
            nonce, key
        )

        if (!success) {
            throw DecryptionFailedException()
        }
        return out
    }

    /**
     * Symmetrically decrypts a message-nonce [pair] using the key [key].
     *
     * **This will zero out both the plaintext and the key inputs when done.**
     */
    @Throws(DecryptionFailedException::class)
    public fun symmetricDecryptSingleMessage(
        pair: MessageNoncePair, key: ByteArray
    ): ByteArray {
        return symmetricDecryptSingleMessage(pair.first, pair.second, key)
    }
}
