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

import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue


// nb: this is mostly making sure everything is called right.
// this doesn't test the actual cryptographic primitives.
/**
 * Tests the cryptographic functions.
 */
class `Test Crypto` {
    @Test
    fun `Test bytearray encryption`() {
        // error checking on bad lengths
        expectThrows<InvalidLengthException> {
            Crypto.symmetricEncryptSingleMessage(byteArrayOf(), byteArrayOf())
        }

        // actual tests
        val message = "-- The Time of the Murder --"
            .toByteArray(Charsets.UTF_8)

        val key1 = "...Just before it happened, I think I saw some red lights. Three of them."
        val realKey1 = key1.deriveEncryptionKey()

        val encrypted = message.encrypt(realKey1)
        val decrypted = encrypted.decrypt(realKey1)
        expectThat(decrypted).isEqualTo(message)

        val key2 = "I thought I'd ask for help, but... just then I was splattered with blood!"
        val realKey2 = key2.deriveEncryptionKey()

        expectThrows<DecryptionFailedException> {
            encrypted.decrypt(realKey2)
        }
    }

    @Test
    fun `Test integrity hashes`() {
        val message1 = "She wasn't dead though... And she struck back at the enemy behind her."
        val hash = message1.integrityHash()
        val verified = message1.verifyIntegrityHash(hash)
        expectThat(verified).isTrue()

        val message2 = "Suddenly, the red lights went out and the whole area was dark."
        expectThat(message2.verifyIntegrityHash(hash)).isFalse()
    }

    @Test
    fun `Test password hashes`() {
        // bit of a long password!
        val password = "...Just at that moment, there was a horrible scream!"
        val hash = password.passwordHash()
        expectThat(password.verifyPasswordHash(hash)).isTrue()

        val incorrectPassword = "Right after that... Dahlia collapsed and I lost consciousness."
        expectThat(incorrectPassword.verifyPasswordHash(hash)).isFalse()

    }
}
