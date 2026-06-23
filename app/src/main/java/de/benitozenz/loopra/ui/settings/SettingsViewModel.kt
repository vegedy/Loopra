package de.benitozenz.loopra.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.benitozenz.loopra.data.db.AppDatabase
import de.benitozenz.loopra.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _maxSteps = MutableStateFlow(100_000)
    val maxSteps: StateFlow<Int> = _maxSteps.asStateFlow()

    private val _tapeSize = MutableStateFlow(100)
    val tapeSize: StateFlow<Int> = _tapeSize.asStateFlow()

    private val _clearDataResult = MutableStateFlow<String?>(null)
    val clearDataResult: StateFlow<String?> = _clearDataResult.asStateFlow()

    fun updateMaxSteps(steps: Int) {
        _maxSteps.value = steps.coerceIn(1000, 1_000_000)
    }

    fun updateTapeSize(size: Int) {
        _tapeSize.value = size.coerceIn(30, 500)
    }

    fun clearAllData() {
        viewModelScope.launch {
            val db = AppDatabase.getInstance(getApplication())
            db.clearAllTables()
            _clearDataResult.value = "All data cleared. Restart the app."
        }
    }

    fun dismissResult() {
        _clearDataResult.value = null
    }
}
