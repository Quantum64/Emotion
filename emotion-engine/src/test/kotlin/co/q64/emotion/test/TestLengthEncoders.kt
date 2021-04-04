package co.q64.emotion.test

import co.q64.emotion.core.util.Codepage
import co.q64.emotion.engine.compress.Compressors
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TestLengthEncoders {

    @Test
    fun `length encoder round trip`() {
        (0..1000).forEach { length ->
            Compressors.decodeLength(
                Codepage.readString(
                    Codepage.encodeBytes(
                        Compressors.encodeLength(length)
                    )
                ).iterator()
            ) shouldBe length
        }
    }

    @Test
    fun `length encoder output size`() {
        (0..254).forEach { Compressors.encodeLength(it).size shouldBe 1 }
        (255..509).forEach { Compressors.encodeLength(it).size shouldBe 2 }
        (510..700).forEach { Compressors.encodeLength(it).size shouldBe 3 }
    }
}