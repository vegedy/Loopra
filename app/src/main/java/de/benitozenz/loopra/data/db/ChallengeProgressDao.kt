package de.benitozenz.loopra.data.db

import androidx.room.*
import de.benitozenz.loopra.data.model.ChallengeProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeProgressDao {

    @Query("SELECT * FROM challenge_progress")
    fun getAllProgress(): Flow<List<ChallengeProgressEntity>>

    @Query("SELECT * FROM challenge_progress WHERE challengeId = :challengeId")
    suspend fun getProgressForChallenge(challengeId: Long): ChallengeProgressEntity?

    @Query("SELECT * FROM challenge_progress WHERE challengeId = :challengeId")
    fun getProgressForChallengeFlow(challengeId: Long): Flow<ChallengeProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ChallengeProgressEntity)

    @Query("SELECT COUNT(*) FROM challenge_progress WHERE completed = 1")
    suspend fun getCompletedCount(): Int
}
