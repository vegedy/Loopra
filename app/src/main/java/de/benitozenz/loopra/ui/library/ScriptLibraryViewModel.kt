package de.benitozenz.loopra.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.benitozenz.loopra.data.db.AppDatabase
import de.benitozenz.loopra.data.model.ScriptEntity
import de.benitozenz.loopra.data.repository.ScriptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ScriptLibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScriptRepository

    val scripts: Flow<List<ScriptEntity>>
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        val dao = AppDatabase.getInstance(application).scriptDao()
        repository = ScriptRepository(dao)
        scripts = repository.allScripts
    }

    fun createAndNavigate(callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createScript()
            callback(id)
        }
    }

    fun deleteScript(script: ScriptEntity) {
        viewModelScope.launch {
            repository.deleteScript(script.id)
        }
    }

    fun toggleFavorite(script: ScriptEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(script.id, script.isFavorite)
        }
    }
}
