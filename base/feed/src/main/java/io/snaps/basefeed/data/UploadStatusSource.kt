package io.snaps.basefeed.data

import android.content.Context
import androidx.annotation.IntRange
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.snaps.corecommon.ext.asFlow
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import video.api.client.api.work.toProgress
import video.api.client.api.work.workers.AbstractUploadWorker
import java.util.UUID
import javax.inject.Inject

interface UploadStatusSource {

    fun listenTo(uploadId: Uuid): Flow<State?>

    sealed class State {

        abstract val uploadId: Uuid

        data class Progress(override val uploadId: Uuid, @IntRange(from = 0L, to = 100L) val progress: Int) : State()
        data class Success(override val uploadId: Uuid) : State()
        data class Error(override val uploadId: Uuid, val error: AppError) : State()
    }
}

class UploadStatusSourceApivideoWorkManagerImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : UploadStatusSource {

    override fun listenTo(uploadId: Uuid): Flow<UploadStatusSource.State?> {
        return WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(UUID.fromString(uploadId)).asFlow()
            .map {
                when (it.state) {
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.BLOCKED -> UploadStatusSource.State.Progress(
                        uploadId = uploadId,
                        progress = 0,
                    )
                    WorkInfo.State.RUNNING -> UploadStatusSource.State.Progress(
                        uploadId = uploadId,
                        progress = it.progress.toProgress(),
                    )
                    WorkInfo.State.SUCCEEDED -> UploadStatusSource.State.Success(uploadId = uploadId)
                    WorkInfo.State.CANCELLED -> UploadStatusSource.State.Error(
                        uploadId = uploadId,
                        error = AppError.Unknown(cause = Exception("Upload canceled!")),
                    )
                    WorkInfo.State.FAILED -> UploadStatusSource.State.Error(
                        uploadId = uploadId,
                        error = AppError.Unknown(
                            cause = Exception(
                                it.outputData.getString(AbstractUploadWorker.ERROR_KEY) ?: "Upload error!",
                            )
                        ),
                    )
                }
            }
    }
}