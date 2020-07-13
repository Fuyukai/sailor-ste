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

package tf.sailor.sste

import java.util.*

private val HEX_ALPHABET = "0123456789abcdef".toCharArray()

/**
 * Encodes this ByteArray in base64.
 */
public fun ByteArray.toBase64(urlSafe: Boolean = true): String {
    val encoder = if (urlSafe) {
        Base64.getUrlEncoder()
    } else {
        Base64.getEncoder()
    }
    return encoder.encodeToString(this)
}

/**
 * Decodes this String from base64.
 */
public fun String.decodeBase64(urlSafe: Boolean = true): ByteArray {
    val decoder = if (urlSafe) {
        Base64.getUrlDecoder()
    } else {
        Base64.getDecoder()
    }
    return decoder.decode(this)
}

/**
 * Encodes this String in base 16.
 */
public fun String.hexlify(): String =
    toByteArray(Charsets.UTF_8).hexlify()

/**
 * Encodes this ByteArray in base 16.
 */
public fun ByteArray.hexlify(): String {
    val output = CharArray(this.size * 2)
    for (idx in indices) {
        val b = this[idx]
        val upper = KotlinSucks.upperBits(b)
        val lower = KotlinSucks.lowerBits(b)
        output[idx] = HEX_ALPHABET[upper]
        output[idx + 1] = HEX_ALPHABET[lower]
    }

    return String(output)
}

/**
 * Decodes this String from base 16.
 */
public fun String.unhexlify(): ByteArray {
    return chunked(2).map { it.toByte(16) }.toTypedArray().toByteArray()
}

