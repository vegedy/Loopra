package de.benitozenz.loopra.data.repository

import de.benitozenz.loopra.data.db.ScriptDao
import de.benitozenz.loopra.data.model.ScriptEntity
import kotlinx.coroutines.flow.Flow

class ScriptRepository(private val dao: ScriptDao) {

    val allScripts: Flow<List<ScriptEntity>> = dao.getAllScripts()

    suspend fun getScriptById(id: Long): ScriptEntity? = dao.getScriptById(id)

    suspend fun createScript(title: String = "Untitled", code: String = ""): Long {
        val script = ScriptEntity(title = title, code = code)
        return dao.insertScript(script)
    }

    suspend fun saveScript(script: ScriptEntity) {
        dao.updateScript(script.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateCode(id: Long, code: String) {
        dao.updateCode(id, code)
    }

    suspend fun updateTitle(id: Long, title: String) {
        dao.updateTitle(id, title)
    }

    suspend fun deleteScript(id: Long) {
        dao.deleteScriptById(id)
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        dao.updateFavorite(id, !currentFavorite)
    }
}
