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

import java.security.MessageDigest

/**
 * Generates an integrity hash for this ByteArray.
 */
public fun ByteArray.integrityHash(): ByteArray =
    Crypto.integrityHash(this)

/**
 * Generates an integrity hash for this String.
 */
public fun String.integrityHash(): ByteArray =
    toByteArray(Charsets.UTF_8).integrityHash()

/**
 * Verifies this ByteArray against an integrity hash.
 */
public fun ByteArray.verifyIntegrityHash(hash: ByteArray): Boolean =
    Crypto.integrityHashVerify(this, hash)

/**
 * Verifies this String against an integrity hash.
 */
public fun String.verifyIntegrityHash(hash: ByteArray): Boolean =
    toByteArray(Charsets.UTF_8).verifyIntegrityHash(hash)

/**
 * Creates an encoded password hash for this ByteArray.
 */
public fun ByteArray.passwordHash(): String =
    Crypto.passwordHash(this)

/**
 * Creates an encoded password hash for this string.
 */
public fun String.passwordHash(): String =
    Crypto.passwordHash(this)

/**
 * Verifies this string against a password hash.
 */
public fun String.verifyPasswordHash(hash: String): Boolean =
    Crypto.passwordVerify(this, hash)

/**
 * Derives a symmetric encryption key from this ByteArray.
 */
public fun ByteArray.deriveEncryptionKey(): ByteArray =
    Crypto.symmetricKeyDerive(this)

/**
 * Derives a symmetric encryption key from this String.
 */
public fun String.deriveEncryptionKey(): ByteArray =
    toByteArray(Charsets.UTF_8).deriveEncryptionKey()

/**
 * Encrypts this ByteArray using the specified key.
 */
public fun ByteArray.encrypt(key: ByteArray): MessageNoncePair =
    Crypto.symmetricEncryptSingleMessage(this, key)

/**
 * Decrypts this ByteArray using the specified key.
 */
public fun MessageNoncePair.decrypt(key: ByteArray): ByteArray =
    Crypto.symmetricDecryptSingleMessage(first, key, second)
