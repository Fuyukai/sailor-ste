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

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

/**
 * Tests codec implementations.
 */
class `Test Codec` {
    // base64 tests aren't done as that is largely pointless, since it's a simple pass-through to
    // Base64.Encoder

    @Test
    fun `Test hexlify`() {
        val inp = "Owning the same nail polish does not a murderer make."
        val hex1 = inp.hexlify()
        expectThat(hex1).equals(listOf(
            "4f776e696e67207468652073616d65206e61696c20706f6c69736820646f6573206e6f",
            "742061206d75726465726572206d616b652e"
        ).joinToString(""))

        val hex2 = listOf(
            "492068617665206265656e20696e20736f6c697461727920636f6e66696e656d656e7420666f72206861",
            "6c66206120796561722e20486f7720636f756c64204920706f69736f6e206865723f"
        ).joinToString("")
        val out = String(hex2.unhexlify())
        expectThat(out).isEqualTo(
            "I have been in solitary confinement for half a year. How could I poison her?"
        )

    }
}
