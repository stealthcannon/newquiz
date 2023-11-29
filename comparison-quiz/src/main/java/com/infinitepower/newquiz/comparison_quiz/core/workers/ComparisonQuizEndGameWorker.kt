package com.infinitepower.newquiz.comparison_quiz.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.infinitepower.newquiz.core.analytics.AnalyticsEvent
import com.infinitepower.newquiz.core.analytics.AnalyticsHelper
import com.infinitepower.newquiz.core.user_services.UserService
import com.infinitepower.newquiz.domain.repository.comparison_quiz.ComparisonQuizRepository
import com.infinitepower.newquiz.model.comparison_quiz.ComparisonMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ComparisonQuizEndGameWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val analyticsHelper: AnalyticsHelper,
    private val userService: UserService,
    private val comparisonQuizRepository: ComparisonQuizRepository,
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val CATEGORY_ID = "category_id"
        private const val COMPARISON_MODE_NAME = "comparison_mode_name"
        private const val END_POSITION = "end_position"

        fun enqueueWork(
            workManager: WorkManager,
            categoryId: String,
            comparisonMode: ComparisonMode,
            endPosition: Int
        ) {
            val data = workDataOf(
                CATEGORY_ID to categoryId,
                COMPARISON_MODE_NAME to comparisonMode.name,
                END_POSITION to endPosition
            )

            val workRequest = OneTimeWorkRequestBuilder<ComparisonQuizEndGameWorker>()
                .setInputData(data)
                .build()

            workManager.enqueue(workRequest)
        }
    }

    override suspend fun doWork(): Result {
        val categoryId = inputData.getString(CATEGORY_ID) ?: return Result.failure()
        val comparisonModeName = inputData.getString(COMPARISON_MODE_NAME) ?: return Result.failure()
        val endPosition = inputData.getInt(END_POSITION, 0)

        analyticsHelper.logEvent(
            AnalyticsEvent.ComparisonQuizGameEnd(
                category = categoryId,
                comparisonMode = comparisonModeName,
                score = endPosition,
            )
        )

        val highestPosition = comparisonQuizRepository.getHighestPosition(categoryId = categoryId)

        userService.saveComparisonQuizGame(
            categoryId = categoryId,
            comparisonMode = comparisonModeName,
            endPosition = endPosition.toUInt(),
            highestPosition = highestPosition.toUInt(),
            generateXp = true
        )

        return Result.success()
    }
}