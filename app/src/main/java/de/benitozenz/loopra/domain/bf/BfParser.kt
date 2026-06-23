package de.benitozenz.loopra.domain.bf

class BfParser {

    data class ParseResult(
        val instructions: List<Instruction>,
        val bracketMap: Map<Int, Int>
    )

    fun parse(code: String): ParseResult {
        val instructions = code.filter { it in Instruction.entries.map { inst -> inst.symbol } }
            .map { Instruction.fromChar(it)!! }

        val bracketMap = mutableMapOf<Int, Int>()
        val stack = ArrayDeque<Int>()

        instructions.forEachIndexed { index, inst ->
            when (inst) {
                Instruction.JUMP_IF_ZERO -> stack.addLast(index)
                Instruction.JUMP_IF_NONZERO -> {
                    val open = stack.removeLastOrNull()
                        ?: throw IllegalArgumentException("Unmatched ']' at position $index")
                    bracketMap[open] = index
                    bracketMap[index] = open
                }
                else -> {}
            }
        }

        if (stack.isNotEmpty()) {
            throw IllegalArgumentException("Unmatched '[' at position ${stack.last()}")
        }

        return ParseResult(instructions, bracketMap)
    }
}
