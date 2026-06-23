package de.benitozenz.loopra.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenge_progress")
data class ChallengeProgressEntity(
    @PrimaryKey val challengeId: Long,
    val completed: Boolean = false,
    val bestSteps: Int = Int.MAX_VALUE,
    val stars: Int = 0,
    val completedAt: Long? = null
)
