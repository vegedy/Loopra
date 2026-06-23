package de.benitozenz.loopra

import de.benitozenz.loopra.domain.bf.BfDebugger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BfDebuggerTest {

    @Test
    fun `step through program`() {
        val debugger = BfDebugger()
        debugger.load("++>+")

        // Step 1: INCREMENT at ip=0, tape[0] = 1, ip=1
        val s1 = debugger.step()
        assertEquals(1, s1.instructionPointer)
        assertEquals(0, s1.dataPointer)
        assertEquals(1, s1.tape[0] ?: 0)

        // Step 2: INCREMENT at ip=1, tape[0] = 2, ip=2
        val s2 = debugger.step()
        assertEquals(2, s2.instructionPointer)
        assertEquals(0, s2.dataPointer)
        assertEquals(2, s2.tape[0] ?: 0)

        // Step 3: MOVE_RIGHT at ip=2, dp=1, tape[0]=2 still, ip=3
        val s3 = debugger.step()
        assertEquals(3, s3.instructionPointer)
        assertEquals(1, s3.dataPointer)
        assertEquals(2, s3.tape[0] ?: 0)

        // Step 4: INCREMENT at ip=3, tape[1] = 1, ip=4
        val s4 = debugger.step()
        assertEquals(4, s4.instructionPointer)
        assertEquals(1, s4.dataPointer)
        assertEquals(1, s4.tape[1] ?: 0)
    }

    @Test
    fun `run to completion`() {
        val debugger = BfDebugger()
        debugger.load("++.")
        val state = debugger.runToCompletion()
        assertTrue(state.isFinished)
        assertEquals(2.toChar().toString(), state.output)
    }

    @Test
    fun `breakpoint stops execution`() {
        val debugger = BfDebugger()
        debugger.load("+++>+++")
        debugger.addBreakpoint(3)
        val state = debugger.runToCompletion()
        assertEquals(3, state.instructionPointer)
        assertFalse(state.isFinished)
    }

    @Test
    fun `toggle breakpoint`() {
        val debugger = BfDebugger()
        debugger.load("+++")
        assertTrue(debugger.toggleBreakpoint(1))
        assertTrue(debugger.getBreakpoints().contains(1))
        assertFalse(debugger.toggleBreakpoint(1))
        assertFalse(debugger.getBreakpoints().contains(1))
    }

    @Test
    fun `reset clears state`() {
        val debugger = BfDebugger()
        debugger.load("+++")
        debugger.runToCompletion()
        debugger.reset()
        val state = debugger.currentState()
        assertEquals(0, state.instructionPointer)
        assertFalse(state.isFinished)
    }
}
