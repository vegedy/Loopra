package de.benitozenz.loopra.ui.debug

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import de.benitozenz.loopra.data.db.AppDatabase
import de.benitozenz.loopra.data.repository.ScriptRepository
import de.benitozenz.loopra.domain.bf.BfDebugger
import de.benitozenz.loopra.domain.bf.DebugState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TapeCell(
    val index: Int,
    val value: Int,
    val isDataPointer: Boolean
)

class DebugViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository: ScriptRepository
    private val debugger = BfDebugger()
    private val scriptId: Long = savedStateHandle.get<Long>("scriptId") ?: -1L

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    private val _tapeCells = MutableStateFlow<List<TapeCell>>(emptyList())
    val tapeCells: StateFlow<List<TapeCell>> = _tapeCells.asStateFlow()

    private val _currentIp = MutableStateFlow(-1)
    val currentIp: StateFlow<Int> = _currentIp.asStateFlow()

    private val _output = MutableStateFlow("")
    val output: StateFlow<String> = _output.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _breakpoints = MutableStateFlow<Set<Int>>(emptySet())
    val breakpoints: StateFlow<Set<Int>> = _breakpoints.asStateFlow()

    init {
        val dao = AppDatabase.getInstance(application).scriptDao()
        repository = ScriptRepository(dao)
        loadScript()
    }

    private fun loadScript() {
        viewModelScope.launch {
            val script = repository.getScriptById(scriptId) ?: return@launch
            _code.value = script.code
            debugger.load(script.code)
            updateTape()
        }
    }

    fun step() {
        if (_isFinished.value) return
        val state = debugger.step()
        applyDebugState(state)
    }

    fun continueExecution() {
        if (_isFinished.value) return
        _isRunning.value = true
        viewModelScope.launch {
            val state = debugger.runToCompletion()
            applyDebugState(state)
            _isRunning.value = false
        }
    }

    fun stop() {
        debugger.reset()
        applyDebugState(debugger.currentState())
        _isFinished.value = false
        _output.value = ""
        _error.value = null
    }

    fun toggleBreakpoint(ip: Int) {
        debugger.toggleBreakpoint(ip)
        _breakpoints.value = debugger.getBreakpoints()
    }

    private fun applyDebugState(state: DebugState) {
        _currentIp.value = state.instructionPointer
        _output.value = state.output
        _isFinished.value = state.isFinished
        _error.value = state.error
        updateTape()
    }

    private fun updateTape() {
        val state = debugger.currentState()
        val dp = state.dataPointer
        val start = (dp - 10).coerceAtLeast(0)
        val end = dp + 10
        val cells = (start..end).map { index ->
            TapeCell(
                index = index,
                value = state.tape[index] ?: 0,
                isDataPointer = index == dp
            )
        }
        _tapeCells.value = cells
    }
}
