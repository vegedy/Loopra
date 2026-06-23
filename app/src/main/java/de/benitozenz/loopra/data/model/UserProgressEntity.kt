package de.benitozenz.loopra.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lastActiveDate: Long = 0,
    val scriptsCreated: Int = 0,
    val challengesCompleted: Int = 0,
    val totalStepsDebugged: Int = 0
)
