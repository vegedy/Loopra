package de.benitozenz.loopra.domain.bf

import kotlinx.coroutines.*

class BfDebugger(
    private val tapeSize: Int = 100,
    private val maxSteps: Int = 100_000,
    private val timeoutMs: Long = 5000L
) {
    private var instructions: List<Instruction> = emptyList()
    private var bracketMap: Map<Int, Int> = emptyMap()
    private val tape = IntArray(tapeSize)
    private var dataPointer = 0
    private var instructionPointer = 0
    private var steps = 0
    private val output = StringBuilder()
    private val breakpoints = mutableSetOf<Int>()
    private var finished = false
    private var error: String? = null

    fun load(code: String) {
        val parseResult = BfParser().parse(code)
        instructions = parseResult.instructions
        bracketMap = parseResult.bracketMap
        reset()
    }

    fun reset() {
        tape.fill(0)
        dataPointer = 0
        instructionPointer = 0
        steps = 0
        output.clear()
        finished = false
        error = null
    }

    fun addBreakpoint(position: Int) {
        breakpoints.add(position)
    }

    fun removeBreakpoint(position: Int) {
        breakpoints.remove(position)
    }

    fun toggleBreakpoint(position: Int): Boolean {
        return if (breakpoints.contains(position)) {
            breakpoints.remove(position)
            false
        } else {
            breakpoints.add(position)
            true
        }
    }

    fun getBreakpoints(): Set<Int> = breakpoints.toSet()

    fun step(): DebugState {
        if (finished || instructionPointer >= instructions.size) {
            finished = true
            return currentState(isRunning = false, isFinished = true)
        }

        executeSingleInstruction()

        if (instructionPointer >= instructions.size) {
            finished = true
        }

        return currentState(isRunning = false, isFinished = finished)
    }

    suspend fun continueExecution(
        onState: suspend (DebugState) -> Unit = {}
    ): DebugState = withTimeout(timeoutMs) {
        while (instructionPointer < instructions.size && !finished) {
            if (instructionPointer in breakpoints && steps > 0) {
                return@withTimeout currentState(isRunning = false, isFinished = false)
            }

            executeSingleInstruction()
            onState(currentState(isRunning = true, isFinished = false))
        }
        finished = true
        currentState(isRunning = false, isFinished = true)
    }

    fun runToCompletion(): DebugState {
        while (instructionPointer < instructions.size && steps < maxSteps) {
            if (instructionPointer in breakpoints && steps > 0) break
            executeSingleInstruction()
        }
        if (steps >= maxSteps) {
            error = "Max steps ($maxSteps) exceeded"
        }
        finished = instructionPointer >= instructions.size
        return currentState(isRunning = false, isFinished = finished)
    }

    fun currentState(isRunning: Boolean = false, isFinished: Boolean = false): DebugState {
        val tapeMap = mutableMapOf<Int, Int>()
        for (i in tape.indices) {
            if (tape[i] != 0) tapeMap[i] = tape[i]
        }
        return DebugState(
            instructionPointer = instructionPointer,
            dataPointer = dataPointer,
            tape = tapeMap,
            output = output.toString(),
            isRunning = isRunning,
            isFinished = isFinished,
            error = error
        )
    }

    private fun executeSingleInstruction() {
        if (steps >= maxSteps) {
            error = "Max steps ($maxSteps) exceeded"
            finished = true
            return
        }
        if (instructionPointer >= instructions.size) {
            finished = true
            return
        }

        val inst = instructions[instructionPointer]
        when (inst) {
            Instruction.MOVE_RIGHT -> dataPointer = (dataPointer + 1).coerceAtMost(tapeSize - 1)
            Instruction.MOVE_LEFT -> dataPointer = (dataPointer - 1).coerceAtLeast(0)
            Instruction.INCREMENT -> tape[dataPointer] = (tape[dataPointer] + 1) and 0xFF
            Instruction.DECREMENT -> tape[dataPointer] = (tape[dataPointer] - 1) and 0xFF
            Instruction.OUTPUT -> output.append(tape[dataPointer].toChar())
            Instruction.INPUT -> { /* no input in debug mode — leave cell as 0 */ }
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
    }
}
