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
import strikt.api.expectThat
import strikt.assertions.*

class `Test UriExt` {
    @Test
    fun `Test named parameters`() {
        val uri = uri(
            scheme = "https", host = "www.youtube.com", path = "/watch", query = "v=KmgTRCuEQy4"
        )

        expectThat(uri.userInfo).isNullOrEmpty()
        val copy = uri.copy(port = 443)

        expectThat(copy.host).isEqualTo(uri.host)
        expectThat(copy.port).isNotEqualTo(uri.port).isEqualTo(443)
    }

    @Test
    fun `Test query param parsing`() {
        val uri = uri(path = "/", query = "a=b&a=q&c&d==1")
        val queryParams = uri.parseQueryParams()

        expectThat(queryParams) {
            // basic check
            get { size }.isEqualTo(4)

            // multiple values
            get { get("a") }.isEqualTo(setOf("b", "q"))

            // empty query parameter
            get { getOne("c") }.isNotNull().isEmpty()

            // urlencoded
            get { getOne("d") }.isNotNull().isEqualTo("=1")
        }
    }
}
