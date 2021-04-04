package co.q64.emotion.test

import co.q64.emotion.core.math.Rational
import co.q64.emotion.core.math.rational
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TestRational {
    @Test
    fun `test single digit print`() {
        2.rational().toString() shouldBe "2"
    }
}