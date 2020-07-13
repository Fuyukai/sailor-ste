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

/**
 * Converts this String from mixedCase to another case separated by a delimiter.
 */
public fun String.mixedToAny(delim: String): String {
    val builder = StringBuilder()

    // simple algorithm that works by finding an uppercase Char, lowercasing it,
    // then appending delim + lowercase

    for ((idx, char) in withIndex()) {

        if (char.isUpperCase()) {
            val lower = char.toLowerCase()
            if (idx > 0) builder.append(delim)
            builder.append(lower)
        } else {
            builder.append(char)
        }
    }

    return builder.toString()
}

/**
 * Converts this String from mixedCase to snake_case.
 */
public fun String.mixedToSnakeCase(): String {
    return mixedToAny("_")
}

/**
 * Converts this String from mixedCase to kebab-base.
 */
public fun String.mixedToKebabCase(): String {
    return mixedToAny("-")
}

