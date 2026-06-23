package de.benitozenz.loopra.data.db

import androidx.room.*
import de.benitozenz.loopra.data.model.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    @Query("SELECT * FROM challenges ORDER BY sortOrder ASC")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getChallengeById(id: Long): ChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(challenges: List<ChallengeEntity>)

    @Query("SELECT COUNT(*) FROM challenges")
    suspend fun getCount(): Int
}
