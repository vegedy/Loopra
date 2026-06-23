package de.benitozenz.loopra.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String,
    val code: String,
    val expectedOutput: String,
    val difficulty: String,
    val optimalSteps: Int,
    val goodSteps: Int,
    val maxSteps: Int,
    val sortOrder: Int
)
