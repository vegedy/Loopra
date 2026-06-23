package de.benitozenz.loopra

import de.benitozenz.loopra.domain.bf.BfParser
import de.benitozenz.loopra.domain.bf.Instruction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BfParserTest {

    private val parser = BfParser()

    @Test
    fun `parse empty string`() {
        val result = parser.parse("")
        assertEquals(0, result.instructions.size)
        assertEquals(0, result.bracketMap.size)
    }

    @Test
    fun `parse simple commands`() {
        val result = parser.parse("><+-.,[]")
        assertEquals(8, result.instructions.size)
        assertEquals(Instruction.MOVE_RIGHT, result.instructions[0])
        assertEquals(Instruction.MOVE_LEFT, result.instructions[1])
        assertEquals(Instruction.INCREMENT, result.instructions[2])
        assertEquals(Instruction.DECREMENT, result.instructions[3])
        assertEquals(Instruction.OUTPUT, result.instructions[4])
        assertEquals(Instruction.INPUT, result.instructions[5])
        assertEquals(Instruction.JUMP_IF_ZERO, result.instructions[6])
        assertEquals(Instruction.JUMP_IF_NONZERO, result.instructions[7])
    }

    @Test
    fun `ignore non bf characters`() {
        val result = parser.parse("a>b<c[d]e+f-g.h,i")
        assertEquals(8, result.instructions.size)
    }

    @Test
    fun `parse brackets and build map`() {
        val result = parser.parse("++[>++[>++<-]<-]")
        val map = result.bracketMap
        assertEquals(map[2], map.entries.first { it.value == 2 }.key)
    }

    @Test
    fun `unmatched opening bracket throws`() {
        assertThrows(IllegalArgumentException::class.java) {
            parser.parse("[++")
        }
    }

    @Test
    fun `unmatched closing bracket throws`() {
        assertThrows(IllegalArgumentException::class.java) {
            parser.parse("++]")
        }
    }

    @Test
    fun `parse hello world`() {
        val code = "++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++."
        val result = parser.parse(code)
        assert(result.instructions.isNotEmpty())
    }
}
