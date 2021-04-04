package co.q64.emotion.meta

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MUTABLE_MAP
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

fun generate(slots: List<Slot>, destination: File) {
    val functions = slots
        .groupBy { it.index / 100 }
        .mapKeys { (index, _) -> "register$index" }

    val file = FileSpec.builder("co.q64.emotion.generated", "OpcodeMap")
        .addType(
            TypeSpec.objectBuilder("OpcodeMap")
                .addProperty(
                    PropertySpec.builder(
                        "slots", INT, KModifier.CONST
                    )
                        .initializer("%L", slots.map { it.index }.max())
                        .build()
                )
                .addProperty(
                    PropertySpec.builder(
                        "ids",
                        MUTABLE_MAP.parameterizedBy(STRING, INT)
                    )
                        .initializer(CodeBlock.of("mutableMapOf<String, Int>()"))
                        .build()
                )
                .addInitializerBlock(
                    CodeBlock.builder()
                        .let { initial ->
                            functions.keys.fold(initial) { builder, function ->
                                builder.addStatement("%L()", function)
                            }
                        }
                        .build()
                )
                .let { initial ->
                    functions.entries.fold(initial) { builder, (name, slots) ->
                        builder.addFunction(
                            FunSpec.builder(name)
                                .addModifiers(KModifier.PRIVATE)
                                .let { initial ->
                                    slots.fold(initial) { builder, slot ->
                                        slot.opcodes.fold(builder) { function, opcode ->
                                            function.addStatement("ids.put(%S, %L)", opcode.id, slot.index)
                                        }
                                    }
                                }
                                .build()
                        )
                    }
                }
                .build()
        )
        .build()

    file.writeTo(destination)
}