package de.benitozenz.loopra.domain.bf

enum class Instruction(val symbol: Char) {
    MOVE_RIGHT('>'),
    MOVE_LEFT('<'),
    INCREMENT('+'),
    DECREMENT('-'),
    OUTPUT('.'),
    INPUT(','),
    JUMP_IF_ZERO('['),
    JUMP_IF_NONZERO(']');

    companion object {
        private val map = entries.associateBy { it.symbol }
        fun fromChar(c: Char): Instruction? = map[c]
    }
}
