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

