package de.benitozenz.loopra.domain.bf

data class DebugState(
    val instructionPointer: Int,
    val dataPointer: Int,
    val tape: Map<Int, Int>,
    val output: String,
    val isRunning: Boolean,
    val isFinished: Boolean,
    val error: String? = null
)
