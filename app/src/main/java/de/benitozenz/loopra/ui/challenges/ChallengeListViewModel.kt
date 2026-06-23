package de.benitozenz.loopra.ui.challenges

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.benitozenz.loopra.data.db.AppDatabase
import de.benitozenz.loopra.data.model.ChallengeEntity
import de.benitozenz.loopra.data.model.ChallengeProgressEntity
import de.benitozenz.loopra.data.repository.ChallengeRepository
import de.benitozenz.loopra.data.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class ChallengeWithProgress(
    val challenge: ChallengeEntity,
    val progress: ChallengeProgressEntity?
)

class ChallengeListViewModel(application: Application) : AndroidViewModel(application) {

    private val challengeRepo: ChallengeRepository
    private val progressRepo: ProgressRepository

    val challengesWithProgress: Flow<List<ChallengeWithProgress>>

    init {
        val db = AppDatabase.getInstance(application)
        challengeRepo = ChallengeRepository(db.challengeDao())
        progressRepo = ProgressRepository(db.challengeProgressDao(), db.userProgressDao())

        challengesWithProgress = combine(
            challengeRepo.allChallenges,
            progressRepo.getAllChallengeProgress()
        ) { challenges, progressList ->
            val progressMap = progressList.associateBy { it.challengeId }
            challenges.map { challenge ->
                ChallengeWithProgress(
                    challenge = challenge,
                    progress = progressMap[challenge.id]
                )
            }
        }
    }
}
