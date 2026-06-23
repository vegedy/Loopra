package de.benitozenz.loopra

import de.benitozenz.loopra.domain.bf.BfInterpreter
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BfInterpreterTest {

    private val interpreter = BfInterpreter()

    @Test
    fun `empty program produces empty output`() = runTest {
        val result = interpreter.execute("")
        assertTrue(result.success)
        assertEquals("", result.output)
    }

    @Test
    fun `increment and output`() = runTest {
        val result = interpreter.execute("+++++.")
        assertTrue(result.success)
        assertEquals(5.toChar().toString(), result.output)
    }

    @Test
    fun `hello world`() = runTest {
        val code = "++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++."
        val result = interpreter.execute(code)
        assertTrue(result.success)
        assertEquals("Hello World!\n", result.output)
    }

    @Test
    fun `loop to zero`() = runTest {
        val result = interpreter.execute("+++[-]")
        assertTrue(result.success)
        assertEquals(0, result.finalTape[0] ?: 0)
    }

    @Test
    fun `simple addition`() = runTest {
        val result = interpreter.execute("+++>+++++[<+>-]<.")
        assertTrue(result.success)
        assertEquals((3 + 5).toChar().toString(), result.output)
    }

    @Test
    fun `max steps exceeded`() = runTest {
        val interpreter = BfInterpreter(maxSteps = 10)
        val result = interpreter.execute("+[]")
        assertTrue(!result.success)
        assertEquals("Max steps (10) exceeded", result.error)
    }

    @Test
    fun `input is read`() = runTest {
        val result = interpreter.execute(",.", input = "A")
        assertTrue(result.success)
        assertEquals("A", result.output)
    }

    @Test
    fun `unmatched brackets return error`() = runTest {
        val result = interpreter.execute("[")
        assertTrue(!result.success)
        assertTrue(result.error?.contains("Unmatched") == true)
    }
}
