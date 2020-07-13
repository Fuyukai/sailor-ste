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
