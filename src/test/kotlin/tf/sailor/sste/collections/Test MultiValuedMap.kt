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

package tf.sailor.sste.collections

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.*
import tf.sailor.sste.collection.multiMapOf
import tf.sailor.sste.collection.mutableMultiMapOf

/**
 * Tests the [MultiValuedMap] object.
 */
class `Test MultiValuedMap` {
    @Test
    fun `Test access functions`() {
        val map = multiMapOf(
            // every 15 boss is good!
            "lolk" to "seiran",
            "lolk" to "ringo",
            "lolk" to "doremy",
            "lolk" to "sagume",
            "lolk" to "clownpiece",
            "lolk" to "junko",
            "lolk" to "hecatia",

            // the only good boss in 16
            "hsifs" to "eternity larva",
        )

        expectThat(map) {
            get { size }.isEqualTo(8)
            get { containsKey("lolk") }.isTrue()
            get { get("lolk") }.isNotEmpty().hasSize(7)

            get { containsKey("hsifs") }.isTrue()
            get { get("hsifs") }.isNotEmpty().hasSize(1)
            get { getOne("hsifs") }.isNotNull().isEqualTo("eternity larva")

            get { containsKey("wbabc") }.isFalse()
            get { get("wbabc") }.isEmpty()
            get { getOne("wbabcx") }.isNull()
        }
    }

    @Test
    fun `Test mutation functions`() {
        val map = mutableMultiMapOf(
            "lolk" to "clownpiece",
            "lolk" to "junko",
            "lolk" to "hecatia"
        )

        expectThat(map["lolk"]).isNotEmpty().hasSize(3)
        map.add("lolk", "serian")
        expectThat(map["lolk"]).isNotEmpty().hasSize(4)
        map.addAll("lolk", "ringo", "doremy", "sagume")
        expectThat(map["lolk"]).isNotEmpty().hasSize(7)


        map.overwrite("lolk", "is the best game")

        expectThat(map) {
            get { get("lolk") }.isNotEmpty().hasSize(1)
            get { getOne("lolk") }.isEqualTo("is the best game")
        }

        map.removeAll("lolk")
        expect {
            that(map["lolk"]).isEmpty()
            that(map.getOne("lolk")).isNull()
        }
    }
}
