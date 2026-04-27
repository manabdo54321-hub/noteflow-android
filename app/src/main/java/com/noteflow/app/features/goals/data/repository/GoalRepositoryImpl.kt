package com.noteflow.app.features.goals.data.repository

import com.noteflow.app.features.goals.data.local.GoalDao
import com.noteflow.app.features.goals.data.local.GoalEntity
import com.noteflow.app.features.goals.domain.model.Goal
import com.noteflow.app.features.goals.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> =
        goalDao.getAllGoals().map { list -> list.map { it.toDomain() } }

    override suspend fun getGoalById(id: Long): Goal? =
        goalDao.getGoalById(id)?.toDomain()

    override suspend fun insertGoal(goal: Goal): Long =
        goalDao.insertGoal(GoalEntity.fromDomain(goal))

    override suspend fun updateGoal(goal: Goal) =
        goalDao.updateGoal(GoalEntity.fromDomain(goal))

    override suspend fun deleteGoal(goal: Goal) =
        goalDao.deleteGoal(GoalEntity.fromDomain(goal))

    override suspend fun deleteGoalById(id: Long) =
        goalDao.deleteGoalById(id)
}
