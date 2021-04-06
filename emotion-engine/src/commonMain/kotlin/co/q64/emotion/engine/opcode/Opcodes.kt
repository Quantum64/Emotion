package co.q64.emotion.engine.opcode

import co.q64.emotion.core.lang.PendingInstruction
import co.q64.emotion.core.opcode.Opcode
import co.q64.emotion.core.opcode.OpcodeLibrary
import co.q64.emotion.core.opcode.OpcodeMarker
import co.q64.emotion.core.util.Codepage
import co.q64.emotion.generated.OpcodeMap

object Opcodes {
    private const val Cutoff = 156

    private val slots = OpcodeMap.ids
        .asSequence()
        .groupBy { (_, id) -> id }
        .mapValues { (_, value) ->
            value
                .asSequence()
                .map { (name, _) -> name }
                .mapNotNull { name -> OpcodeLibrary.names[name] }
                .toList()
        }

    fun find(name: String) = OpcodeLibrary.names[name]
    fun find(marker: OpcodeMarker) = OpcodeLibrary.markers[marker]
        ?: error("Missing opcode with marker $marker")

    fun encode(opcode: Opcode): String = OpcodeMap.ids[opcode.id]?.let { index ->
        (if (index < Cutoff) sequenceOf(index)
        else sequenceOf(((index - Cutoff) / 256) + 156, (index - Cutoff) % 256))
            .map { Codepage.fromId(it) }
            .joinToString("") { it.character }
    } ?: error("Opcode ${opcode.id} missing in slots map")

    fun decode(characters: List<Codepage>) = when {
        characters.isEmpty() -> null
        characters.first().id < Cutoff -> characters.first().id
        characters.size < 2 -> null
        else -> ((characters.first().id - Cutoff) * 256 + Cutoff) + characters.last().id
    }
        .let { slots[it] ?: emptyList() }
        .let { PendingInstruction(it) }

    fun isInstruction(characters: List<Codepage>) =
        (characters.size == 1 && characters.first().id < Cutoff) ||
                characters.size == 2
}