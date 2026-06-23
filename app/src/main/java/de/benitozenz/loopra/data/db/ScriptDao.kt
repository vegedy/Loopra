package de.benitozenz.loopra.data.db

import androidx.room.*
import de.benitozenz.loopra.data.model.ScriptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {

    @Query("SELECT * FROM scripts ORDER BY updatedAt DESC")
    fun getAllScripts(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM scripts WHERE id = :id")
    suspend fun getScriptById(id: Long): ScriptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: ScriptEntity): Long

    @Update
    suspend fun updateScript(script: ScriptEntity)

    @Delete
    suspend fun deleteScript(script: ScriptEntity)

    @Query("DELETE FROM scripts WHERE id = :id")
    suspend fun deleteScriptById(id: Long)

    @Query("UPDATE scripts SET title = :title, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE scripts SET code = :code, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCode(id: Long, code: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE scripts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM scripts")
    suspend fun getScriptCount(): Int
}
