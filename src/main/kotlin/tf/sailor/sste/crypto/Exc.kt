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

import kotlin.contracts.contract

/**
 * Base class for encryption exceptions.
 */
public open class EncryptionException(message: String) : Exception(message)

/**
 * Thrown when a libsodium method fails miscellaneously.
 */
public class SodiumException(method: String) : EncryptionException("libsodium $method failed")

internal fun sodiumFailed(method: String): Nothing = throw SodiumException(method)

/**
 * Thrown when decryption fails.
 */
public class DecryptionFailedException : EncryptionException("Decryption failed")

/**
 * Thrown when a length is invalid.
 */
public class InvalidLengthException(
    name: String
) : EncryptionException("Input $name is invalid length")

internal fun lengthCheck(cond: Boolean, name: String) {
    if (!cond) throw InvalidLengthException(name)
}
