package de.benitozenz.loopra.domain.bf

data class ExecutionResult(
    val output: String,
    val stepsExecuted: Int,
    val success: Boolean,
    val error: String? = null,
    val finalTape: Map<Int, Int> = emptyMap(),
    val finalDataPointer: Int = 0
)
