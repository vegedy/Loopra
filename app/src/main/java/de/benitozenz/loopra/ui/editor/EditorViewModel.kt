package de.benitozenz.loopra.ui.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import de.benitozenz.loopra.data.db.AppDatabase
import de.benitozenz.loopra.data.model.ScriptEntity
import de.benitozenz.loopra.data.repository.ScriptRepository
import de.benitozenz.loopra.domain.bf.BfInterpreter
import de.benitozenz.loopra.domain.bf.ExecutionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository: ScriptRepository
    private val interpreter = BfInterpreter()
    private val scriptId: Long = savedStateHandle.get<Long>("scriptId") ?: -1L

    private val _script = MutableStateFlow<ScriptEntity?>(null)
    val script: StateFlow<ScriptEntity?> = _script.asStateFlow()

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    private val _output = MutableStateFlow("")
    val output: StateFlow<String> = _output.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var saveJob: Job? = null
    private var currentScript: ScriptEntity? = null

    init {
        val dao = AppDatabase.getInstance(application).scriptDao()
        repository = ScriptRepository(dao)
        loadOrCreateScript()
    }

    private fun loadOrCreateScript() {
        viewModelScope.launch {
            if (scriptId == -1L) {
                val id = repository.createScript()
                val newScript = repository.getScriptById(id)
                currentScript = newScript
                _script.value = newScript
                _code.value = newScript?.code ?: ""
            } else {
                val existing = repository.getScriptById(scriptId)
                currentScript = existing
                _script.value = existing
                _code.value = existing?.code ?: ""
            }
        }
    }

    fun updateCode(newCode: String) {
        _code.value = newCode
        currentScript?.let { script ->
            saveJob?.cancel()
            saveJob = viewModelScope.launch {
                repository.updateCode(script.id, newCode)
                _script.value = script.copy(code = newCode, updatedAt = System.currentTimeMillis())
            }
        }
    }

    fun updateTitle(newTitle: String) {
        currentScript?.let { script ->
            viewModelScope.launch {
                repository.updateTitle(script.id, newTitle)
                _script.value = script.copy(title = newTitle, updatedAt = System.currentTimeMillis())
            }
        }
    }

    fun runCode() {
        val codeToRun = _code.value
        if (codeToRun.isBlank() || _isRunning.value) return

        _isRunning.value = true
        _output.value = ""
        _error.value = null

        viewModelScope.launch {
            val result: ExecutionResult = try {
                interpreter.execute(codeToRun)
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                _isRunning.value = false
                return@launch
            }

            _output.value = result.output
            _isRunning.value = false
            if (!result.success) {
                _error.value = result.error
            }
        }
    }

    fun clearOutput() {
        _output.value = ""
        _error.value = null
    }
}
