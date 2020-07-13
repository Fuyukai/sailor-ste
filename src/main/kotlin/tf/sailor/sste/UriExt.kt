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

import tf.sailor.sste.collection.MultiValuedMap
import tf.sailor.sste.collection.multiMapOf
import tf.sailor.sste.collection.mutableMultiValuedMapOf
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.nio.charset.Charset

/**
 * Helper function for constructing a [URI] using named parameters and default values.
 *
 * This avoids the annoyance of having to construct a URI manually or passing all 6 values to the
 * URI constructor.
 */
@Throws(URISyntaxException::class)
public fun uri(
    scheme: String? = null,
    userInfo: String? = null,
    host: String? = null,
    port: Int = -1,
    path: String? = null,
    query: String? = null,
    fragment: String? = null,
): URI = URI(scheme, userInfo, host, port, path, query, fragment)


/**
 * Copies a URI, replacing its properties with the specified ones.
 */
@Throws(URISyntaxException::class)
public fun URI.copy(
    scheme: String? = null,
    userInfo: String? = null,
    host: String? = null,
    port: Int = -1,
    path: String? = null,
    query: String? = null,
    fragment: String? = null,
): URI {
    return uri(
        scheme = scheme ?: this.scheme,
        userInfo = userInfo ?: this.userInfo,
        host = host ?: this.host,
        port = if (port > -1) port else this.port,
        path = path ?: this.path,
        query = query ?: this.query,
        fragment = fragment ?: this.fragment
    )
}

/**
 * Creates a new [MultiValuedMap] containing the query parameters of this URI.
 *
 * If a parameter with no value is present, it will be represented by the empty string.
 *
 * @param charset: The [Charset] to decode URL parameters with.
 */
public fun URI.parseQueryParams(charset: Charset = Charsets.UTF_8): MultiValuedMap<String, String> {
    val map = mutableMultiValuedMapOf<String, String>()

    // this uses the raw query as not to have url decoded & or = mess stuff up
    val query = rawQuery ?: return multiMapOf()
    val split = query.split("&")

    for (pair in split) {
        if (!pair.contains("=")) {
            map.add(pair, "")
            continue
        }

        val splitPair = pair.split("=", limit = 2)
        val rawName = splitPair.first()
        val value = if (splitPair.size == 2) {
            val rawValue = splitPair[1]
            URLDecoder.decode(rawValue, charset)
        } else {
            ""
        }

        val name = URLDecoder.decode(rawName, charset)
        map.add(name, value)
    }

    return map
}
