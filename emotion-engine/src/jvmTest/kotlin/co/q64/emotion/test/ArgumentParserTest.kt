package co.q64.emotion.test

import co.q64.emotion.core.math.rational
import co.q64.emotion.core.value.listOfValues
import co.q64.emotion.core.value.parseArguments
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ArgumentParserTest {
    @Test
    fun `simple arguments`() {
        parseArguments("1, hello, 2, world") shouldBe listOfValues(1, "hello", 2, "world")
        parseArguments("0.5") shouldBe listOfValues(0.5.rational())
        parseArguments("1/2") shouldBe listOfValues(0.5.rational())
    }

    @Test
    fun `quoted arguments`() {
        parseArguments("""foo, "hello world", bar""") shouldBe listOfValues("foo", "hello world", "bar")
    }

    @Test
    fun `list arguments`() {
        parseArguments("[1, 2, 3, 4, 5]") shouldBe listOfValues(listOfValues(1, 2, 3, 4, 5))
        parseArguments("[hello, world]") shouldBe listOfValues(listOfValues("hello", "world"))
        parseArguments("[foo, 42]") shouldBe listOfValues(listOfValues("foo", 42))
        parseArguments("[foo], [bar]") shouldBe listOfValues(listOfValues("foo"), listOfValues("bar"))
    }

    @Test
    fun `nested list arguments`() {
        parseArguments("[[1, hello], [2, world]]") shouldBe listOfValues(
            listOfValues(
                listOfValues(1, "hello"),
                listOfValues(2, "world")
            )
        )
    }

    @Test
    fun `quotes in list`() {
        parseArguments("""["this is a", quoted, "string in a list"]""") shouldBe listOfValues(
            listOfValues(
                "this is a",
                "quoted",
                "string in a list"
            )
        )
    }

    @Test
    fun `number in quotes should be string`() {
        parseArguments(""" "42" """) shouldBe listOfValues("42")
    }

    @Test
    fun `empty arguments should parse to empty list`() {
        parseArguments("") shouldBe emptyList()
    }
}