package de.benitozenz.loopra.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.benitozenz.loopra.data.db.AppDatabase
import de.benitozenz.loopra.data.model.UserProgressEntity
import de.benitozenz.loopra.data.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Badge(
    val id: String,
    val name: String,
    val icon: String,
    val earned: Boolean
)

data class ProfileState(
    val level: Int = 1,
    val xp: Int = 0,
    val xpForNextLevel: Int = 200,
    val streak: Int = 0,
    val scriptsCreated: Int = 0,
    val challengesCompleted: Int = 0,
    val debugSteps: Int = 0,
    val badges: List<Badge> = emptyList()
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val progressRepo: ProgressRepository

    val profileState: Flow<ProfileState>

    init {
        val db = AppDatabase.getInstance(application)
        progressRepo = ProgressRepository(db.challengeProgressDao(), db.userProgressDao())

        profileState = progressRepo.getUserProgress().map { userProgress ->
            val progress = userProgress ?: UserProgressEntity()
            val xpInLevel = progress.xp % 200
            val badges = computeBadges(progress)
            ProfileState(
                level = progress.level,
                xp = xpInLevel,
                xpForNextLevel = 200,
                streak = progress.streak,
                scriptsCreated = progress.scriptsCreated,
                challengesCompleted = progress.challengesCompleted,
                debugSteps = progress.totalStepsDebugged,
                badges = badges
            )
        }
    }

    private fun computeBadges(progress: UserProgressEntity): List<Badge> {
        return listOf(
            Badge("first_script", "First Script", "\uD83D\uDCDD", progress.scriptsCreated >= 1),
            Badge("script_collector", "Script Collector", "\uD83D\uDCDA", progress.scriptsCreated >= 5),
            Badge("first_challenge", "Challenge Novice", "\uD83C\uDF1F", progress.challengesCompleted >= 1),
            Badge("challenge_master", "Challenge Master", "\uD83C\uDFC6", progress.challengesCompleted >= 3),
            Badge("debugger", "Debugger", "\uD83D\uDD0D", progress.totalStepsDebugged >= 100),
            Badge("streak_3", "On Fire", "\uD83D\uDD25", progress.streak >= 3),
            Badge("streak_7", "Unstoppable", "\u26A1", progress.streak >= 7),
            Badge("level_5", "Level 5", "\u2B50", progress.level >= 5)
        )
    }
}
