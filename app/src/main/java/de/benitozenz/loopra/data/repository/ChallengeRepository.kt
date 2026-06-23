package de.benitozenz.loopra.data.repository

import de.benitozenz.loopra.data.db.ChallengeDao
import de.benitozenz.loopra.data.model.ChallengeEntity
import kotlinx.coroutines.flow.Flow

class ChallengeRepository(private val dao: ChallengeDao) {

    val allChallenges: Flow<List<ChallengeEntity>> = dao.getAllChallenges()

    suspend fun getChallengeById(id: Long): ChallengeEntity? = dao.getChallengeById(id)
}
