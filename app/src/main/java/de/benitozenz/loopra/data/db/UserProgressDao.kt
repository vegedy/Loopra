package de.benitozenz.loopra.data.db

import androidx.room.*
import de.benitozenz.loopra.data.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getProgress(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getProgressSync(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET xp = :xp, level = :level WHERE id = 1")
    suspend fun updateXpAndLevel(xp: Int, level: Int)

    @Query("UPDATE user_progress SET streak = :streak, lastActiveDate = :lastActive WHERE id = 1")
    suspend fun updateStreak(streak: Int, lastActive: Long)

    @Query("UPDATE user_progress SET scriptsCreated = scriptsCreated + 1 WHERE id = 1")
    suspend fun incrementScriptsCreated()

    @Query("UPDATE user_progress SET challengesCompleted = challengesCompleted + 1 WHERE id = 1")
    suspend fun incrementChallengesCompleted()

    @Query("UPDATE user_progress SET totalStepsDebugged = totalStepsDebugged + :steps WHERE id = 1")
    suspend fun addDebugSteps(steps: Int)
}
