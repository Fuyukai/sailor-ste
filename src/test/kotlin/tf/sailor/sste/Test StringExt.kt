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

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

/**
 * Tests string extensions.
 */
class `Test StringExt` {
    @ParameterizedTest
    @CsvSource(value = ["test, _, test", "testTwo, _, test_two", "TestThree, -, test-three"])
    fun `Test fromMixedCase`(from: String, delim: String, to: String) {
        expectThat(from.mixedToAny(delim)).isEqualTo(to)
    }
}
