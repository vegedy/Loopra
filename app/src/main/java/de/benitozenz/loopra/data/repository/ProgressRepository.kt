package de.benitozenz.loopra.data.repository

import de.benitozenz.loopra.data.db.ChallengeProgressDao
import de.benitozenz.loopra.data.db.UserProgressDao
import de.benitozenz.loopra.data.model.ChallengeProgressEntity
import de.benitozenz.loopra.data.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

class ProgressRepository(
    private val challengeProgressDao: ChallengeProgressDao,
    private val userProgressDao: UserProgressDao
) {

    fun getAllChallengeProgress(): Flow<List<ChallengeProgressEntity>> =
        challengeProgressDao.getAllProgress()

    fun getProgressForChallenge(challengeId: Long): Flow<ChallengeProgressEntity?> =
        challengeProgressDao.getProgressForChallengeFlow(challengeId)

    fun getUserProgress(): Flow<UserProgressEntity?> = userProgressDao.getProgress()

    suspend fun getUserProgressSync(): UserProgressEntity? = userProgressDao.getProgressSync()

    suspend fun recordChallengeAttempt(
        challengeId: Long,
        stepsUsed: Int,
        optimalSteps: Int,
        goodSteps: Int
    ) {
        val existing = challengeProgressDao.getProgressForChallenge(challengeId)
        val completed = true
        val stars = calculateStars(stepsUsed, optimalSteps, goodSteps)
        val bestSteps = minOf(existing?.bestSteps ?: Int.MAX_VALUE, stepsUsed)
        val bestStars = maxOf(existing?.stars ?: 0, stars)

        challengeProgressDao.upsertProgress(
            ChallengeProgressEntity(
                challengeId = challengeId,
                completed = completed,
                bestSteps = bestSteps,
                stars = bestStars,
                completedAt = existing?.completedAt ?: System.currentTimeMillis()
            )
        )

        if (existing?.completed != true) {
            userProgressDao.incrementChallengesCompleted()
        }

        val userProgress = userProgressDao.getProgressSync()
        val xpGain = when {
            stars >= 3 -> 100
            stars >= 2 -> 60
            stars >= 1 -> 30
            else -> 10
        }
        val newXp = (userProgress?.xp ?: 0) + xpGain
        val newLevel = calculateLevel(newXp)
        userProgressDao.updateXpAndLevel(newXp, newLevel)
    }

    private fun calculateStars(steps: Int, optimal: Int, good: Int): Int {
        return when {
            steps <= optimal -> 3
            steps <= good -> 2
            else -> 1
        }
    }

    private fun calculateLevel(xp: Int): Int = (xp / 200).coerceAtLeast(1)

    suspend fun updateStreak(lastActive: Long) {
        val progress = userProgressDao.getProgressSync()
        val currentStreak = progress?.streak ?: 0
        val lastDate = progress?.lastActiveDate ?: 0L

        val newStreak = if (isConsecutiveDay(lastDate, lastActive)) {
            currentStreak + 1
        } else {
            1
        }
        userProgressDao.updateStreak(newStreak, lastActive)
    }

    private fun isConsecutiveDay(last: Long, current: Long): Boolean {
        if (last == 0L) return false
        val diff = current - last
        return diff in 20_000_000..52_000_000 // ~23-60h tolerance
    }

    suspend fun addScriptCreated() = userProgressDao.incrementScriptsCreated()
    suspend fun addDebugSteps(steps: Int) = userProgressDao.addDebugSteps(steps)
}
