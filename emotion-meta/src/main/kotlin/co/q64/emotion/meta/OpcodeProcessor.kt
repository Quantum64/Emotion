package co.q64.emotion.meta

import co.q64.emotion.core.opcode.Opcode
import co.q64.emotion.core.opcode.OpcodeLibrary
import co.q64.emotion.core.value.ValueType
import co.q64.emotion.core.value.any
import co.q64.emotion.core.value.list
import co.q64.emotion.core.value.number
import co.q64.emotion.core.value.string
import kotlin.system.measureTimeMillis

private val DebugPrint = true

data class Slot(
    val index: Int,
    val used: MutableSet<List<ValueType>> = mutableSetOf(),
    val opcodes: MutableList<Opcode> = mutableListOf()
) {
    var consumed = emptySet<String>()

    fun add(opcode: Opcode) {
        used += opcode.values
        opcodes += opcode


/*
        consumed = used
            .asSequence()
            .flatMap { entry ->
                generateSequence(entry) { if (it.isEmpty()) null else it.dropLast(1) }
            }
            .toList()
            .flatMap { it.render() }
            .toSet()

 */
        /*
        consumed = used
            .flatMap { it.render() }
            .toSet()


         */


        //println(index.toString() + " " + consumed)
    }
}


private fun List<ValueType>.render(): List<String> {
    fun options(current: List<ValueType>): List<List<ValueType>> = when {
        current.isEmpty() -> listOf(current)
        else ->
            when (val last = current.first()) {
                any -> listOf(number, list, string)
                else -> listOf(last)
            }.flatMap {
                options(current.drop(1))
                    .map { split -> listOf(it) + split }
            }

    }
    return options(this)
        .map { current ->
            current.joinToString("") {
                when (it) {
                    number -> "n"
                    string -> "s"
                    list -> "l"
                    else -> "u"
                }
            }
        }
}

fun processOpcodes() {
    val slots = mutableListOf<Slot>()


    val time = measureTimeMillis {
        for (opcode in OpcodeLibrary.opcodes) {
            /*
            val render = listOf(opcode.values)
                .asSequence()
                .flatMap { entry ->
                    generateSequence(entry) { if (it.isEmpty()) null else it.dropLast(1) }
                }
                .toList()
                .flatMap { it.render() }
                .toSet()



             */
            //val render = opcode.values.render()

            //   println("${opcode.id}: $render")


            var index = 0
            while (true) {
                if (opcode.values.isEmpty() || index >= slots.size) {
                    val slot = Slot(
                        index = slots.size
                    )
                    slot.add(opcode)
                    slots += slot
                    break
                } else {
                    val slot = slots[index]
                    var skip = false
                    if (emptyList() in slot.used) {
                        skip = true
                    }

                    if (!skip) {
                        for (entry in slot.used) {
                            var unique = kotlin.math.max(entry.size, opcode.values.size)
                            var i = 0
                            while (true) {
                                if (i >= entry.size && i >= opcode.values.size) {
                                    break
                                }
                                val a = opcode.values.getOrNull(i) ?: any
                                val b = entry.getOrNull(i) ?: any
                                if (a == b || a == any || b == any) {
                                    unique--
                                }
                                i++
                            }
                            if (unique <= 0) {
                                skip = true
                                break
                            }
                        }
                    }

                    if (skip) {
                        index++
                        continue
                    }
                    slot.add(opcode)
                    break
                }
            }
        }
    }

    if (DebugPrint) {
        slots.forEach { slot ->
            slot.opcodes.forEach { opcode ->
                println("${slot.index.toString().padStart(3, '0')} -> ${opcode.id}")
            }
        }

        println()
    }
    println("Done in $time ms")
}

fun main() {
    processOpcodes()
}