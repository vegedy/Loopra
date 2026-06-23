package de.benitozenz.loopra.ui.challenges

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import de.benitozenz.loopra.data.db.AppDatabase
import de.benitozenz.loopra.data.model.ChallengeEntity
import de.benitozenz.loopra.data.model.ChallengeProgressEntity
import de.benitozenz.loopra.data.repository.ChallengeRepository
import de.benitozenz.loopra.data.repository.ProgressRepository
import de.benitozenz.loopra.domain.bf.BfInterpreter
import de.benitozenz.loopra.domain.bf.ExecutionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChallengeDetailState(
    val challenge: ChallengeEntity? = null,
    val progress: ChallengeProgressEntity? = null,
    val userCode: String = "",
    val isRunning: Boolean = false,
    val testResult: String? = null,
    val testPassed: Boolean = false,
    val error: String? = null
)

class ChallengeDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val challengeRepo: ChallengeRepository
    private val progressRepo: ProgressRepository
    private val interpreter = BfInterpreter()
    private val challengeId: Long = savedStateHandle.get<Long>("challengeId") ?: -1L

    private val _state = MutableStateFlow(ChallengeDetailState())
    val state: StateFlow<ChallengeDetailState> = _state.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        challengeRepo = ChallengeRepository(db.challengeDao())
        progressRepo = ProgressRepository(db.challengeProgressDao(), db.userProgressDao())
        loadChallenge()
    }

    private fun loadChallenge() {
        viewModelScope.launch {
            val challenge = challengeRepo.getChallengeById(challengeId) ?: return@launch
            _state.value = _state.value.copy(
                challenge = challenge,
                userCode = challenge.code
            )
        }
    }

    fun updateCode(code: String) {
        _state.value = _state.value.copy(userCode = code)
    }

    fun runTest() {
        val challenge = _state.value.challenge ?: return
        val code = _state.value.userCode
        if (code.isBlank() || _state.value.isRunning) return

        _state.value = _state.value.copy(isRunning = true, testResult = null, error = null)

        viewModelScope.launch {
            val result: ExecutionResult = try {
                interpreter.execute(code)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRunning = false,
                    testResult = "Runtime error: ${e.message}",
                    testPassed = false
                )
                return@launch
            }

            val passed = result.success && result.output == challenge.expectedOutput
            val testResult = if (passed) {
                "✓ Passed! (${result.stepsExecuted} steps)"
            } else {
                buildString {
                    appendLine("✗ Failed")
                    if (!result.success) appendLine("Error: ${result.error}")
                    appendLine("Expected: \"${challenge.expectedOutput.escape()}\"")
                    appendLine("Got:      \"${result.output.escape()}\"")
                }
            }

            _state.value = _state.value.copy(
                isRunning = false,
                testResult = testResult,
                testPassed = passed
            )

            if (passed) {
                progressRepo.recordChallengeAttempt(
                    challengeId = challenge.id,
                    stepsUsed = result.stepsExecuted,
                    optimalSteps = challenge.optimalSteps,
                    goodSteps = challenge.goodSteps
                )
                loadProgress()
            }
        }
    }

    private suspend fun loadProgress() {
        val progress = when {
            challengeId >= 0 -> {
                val db = AppDatabase.getInstance(getApplication())
                db.challengeProgressDao().getProgressForChallenge(challengeId)
            }
            else -> null
        }
        _state.value = _state.value.copy(progress = progress)
    }

    private fun String.escape(): String {
        return this.replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .filter { it >= ' ' || it == '\n' || it == '\r' || it == '\t' }
    }
}
