package de.benitozenz.loopra.domain.bf

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.coroutineContext

class BfInterpreter(
    private val tapeSize: Int = 30000,
    private val maxSteps: Int = 100_000,
    private val timeoutMs: Long = 5000L
) {

    suspend fun execute(
        code: String,
        input: String = "",
        onStep: ((Int, Int, Map<Int, Int>, String) -> Unit)? = null
    ): ExecutionResult = withTimeout(timeoutMs) {
        coroutineScope {
            val parseResult = try {
                BfParser().parse(code)
            } catch (e: IllegalArgumentException) {
                return@coroutineScope ExecutionResult(
                    output = "", stepsExecuted = 0, success = false, error = e.message
                )
            }

            val instructions = parseResult.instructions
            val bracketMap = parseResult.bracketMap
            val tape = IntArray(tapeSize)
            var dataPointer = 0
            var instructionPointer = 0
            var steps = 0
            val output = StringBuilder()
            var inputIndex = 0

            while (instructionPointer < instructions.size && steps < maxSteps) {
                if (!coroutineContext.isActive) {
                    return@coroutineScope ExecutionResult(
                        output = output.toString(),
                        stepsExecuted = steps,
                        success = false,
                        error = "Execution cancelled",
                        finalTape = tapeToMap(tape),
                        finalDataPointer = dataPointer
                    )
                }

                val inst = instructions[instructionPointer]

                when (inst) {
                    Instruction.MOVE_RIGHT -> {
                        dataPointer = (dataPointer + 1).coerceAtMost(tapeSize - 1)
                    }
                    Instruction.MOVE_LEFT -> {
                        dataPointer = (dataPointer - 1).coerceAtLeast(0)
                    }
                    Instruction.INCREMENT -> {
                        tape[dataPointer] = (tape[dataPointer] + 1) and 0xFF
                    }
                    Instruction.DECREMENT -> {
                        tape[dataPointer] = (tape[dataPointer] - 1) and 0xFF
                    }
                    Instruction.OUTPUT -> {
                        output.append(tape[dataPointer].toChar())
                    }
                    Instruction.INPUT -> {
                        tape[dataPointer] = if (inputIndex < input.length) {
                            input[inputIndex].code
                        } else {
                            0
                        }
                        inputIndex++
                    }
                    Instruction.JUMP_IF_ZERO -> {
                        if (tape[dataPointer] == 0) {
                            instructionPointer = bracketMap[instructionPointer] ?: instructionPointer
                        }
                    }
                    Instruction.JUMP_IF_NONZERO -> {
                        if (tape[dataPointer] != 0) {
                            instructionPointer = bracketMap[instructionPointer] ?: instructionPointer
                        }
                    }
                }

                instructionPointer++
                steps++
                onStep?.invoke(steps, dataPointer, tapeToMap(tape), output.toString())
            }

            if (steps >= maxSteps) {
                return@coroutineScope ExecutionResult(
                    output = output.toString(),
                    stepsExecuted = steps,
                    success = false,
                    error = "Max steps ($maxSteps) exceeded",
                    finalTape = tapeToMap(tape),
                    finalDataPointer = dataPointer
                )
            }

            ExecutionResult(
                output = output.toString(),
                stepsExecuted = steps,
                success = true,
                finalTape = tapeToMap(tape),
                finalDataPointer = dataPointer
            )
        }
    }

    private fun tapeToMap(tape: IntArray): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        for (i in tape.indices) {
            if (tape[i] != 0) {
                map[i] = tape[i]
            }
        }
        return map
    }
}
