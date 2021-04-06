package co.q64.emotion.test.lower

import co.q64.emotion.core.lang.Instruction
import co.q64.emotion.core.opcode.Control
import co.q64.emotion.engine.compiler.lower.CompressAdjacentEndLowering
import co.q64.emotion.engine.opcode.Opcodes
import co.q64.emotion.test.util.createCompiled
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CompressAdjacentEndTest {
    private val single = Opcodes.find(Control.End)
    private val double = Opcodes.find(Control.Internal.End2)
    private val triple = Opcodes.find(Control.Internal.End3)
    private val add = Opcodes.find("num.add")!!

    @Test
    fun `end instruction compression`() {
        with(CompressAdjacentEndLowering) {
            compress(0) shouldBe emptyList()
            compress(1) shouldBe listOf(single)
            compress(2) shouldBe listOf(double)
            compress(3) shouldBe listOf(triple)
            compress(4) shouldBe listOf(triple, single)
            compress(5) shouldBe listOf(triple, double)
            compress(6) shouldBe listOf(triple, triple)
            compress(7) shouldBe listOf(triple, triple, single)
            compress(8) shouldBe listOf(triple, triple, double)
        }
    }

    @Test
    fun `trailing ends should be discarded`() {
        CompressAdjacentEndLowering.lower(createCompiled(add, single)) shouldBe createCompiled(add)
        CompressAdjacentEndLowering.lower(createCompiled(add, single, single, single)) shouldBe createCompiled(add)
    }

    @Test
    fun `lowering round trip`() {
        createCompiled(add, single, single, single, add, single, single, add, single, single, single, single, add)
            .let { compiled ->
                CompressAdjacentEndLowering.raise(
                    CompressAdjacentEndLowering.lower(compiled).map { Instruction.fixed(it.opcode!!) }
                ) shouldBe compiled.map {
                    Instruction.fixed(it.opcode!!)
                }
            }
    }
}