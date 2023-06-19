package io.snaps.basefeed.data

import android.content.Context
import androidx.annotation.FloatRange
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

    fun listenToByUploadId(uploadId: Uuid): Flow<State>

    /**
     * by video internal id
     */
    fun listenToByVideoId(videoId: Uuid): Flow<State>?

    fun registerUploadId(videoId: Uuid, uploadId: Uuid)

    sealed class State {

        abstract val uploadId: Uuid

        data class Progress(override val uploadId: Uuid, @FloatRange(from = 0.0, to = 1.0) val progress: Float) : State()
        data class Success(override val uploadId: Uuid) : State()
        data class Error(override val uploadId: Uuid, val error: AppError) : State()
    }
}

class UploadStatusSourceApivideoWorkManagerImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : UploadStatusSource {

    private val registry = hashMapOf<Uuid, Uuid>()

    override fun listenToByUploadId(uploadId: Uuid): Flow<UploadStatusSource.State> {
        return WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(UUID.fromString(uploadId)).asFlow()
            .map {
                when (it.state) {
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.BLOCKED -> UploadStatusSource.State.Progress(
                        uploadId = uploadId,
                        progress = 0f,
                    )
                    WorkInfo.State.RUNNING -> UploadStatusSource.State.Progress(
                        uploadId = uploadId,
                        progress = it.progress.toProgress() / 100f,
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

    override fun listenToByVideoId(videoId: Uuid): Flow<UploadStatusSource.State>? {
        return registry[videoId]?.let(::listenToByUploadId)
    }

    override fun registerUploadId(videoId: Uuid, uploadId: Uuid) {
        registry[videoId] = uploadId
    }
}