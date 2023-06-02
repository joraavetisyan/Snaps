package io.snaps.basefeed.data

import android.app.Application
import android.content.Context
import androidx.annotation.IntRange
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.snaps.corecommon.ext.asFlow
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.GlobalRequestObserver
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
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

// todo delete
class UploadStatusSourceGotevUploadServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationCoroutineScope private val coroutineScope: CoroutineScope,
) : UploadStatusSource, RequestObserverDelegate {

    private val _state = MutableStateFlow<UploadStatusSource.State?>(null)

    init {
        GlobalRequestObserver(context as Application, this)
    }

    override fun listenTo(uploadId: Uuid): Flow<UploadStatusSource.State?> {
        return _state.filter { it?.uploadId == uploadId }
    }

    private fun emit(state: UploadStatusSource.State) {
        coroutineScope.launch { _state.emit(state) }
    }

    override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {
        emit(
            UploadStatusSource.State.Error(
                uploadId = uploadInfo.uploadId,
                error = AppError.Unknown(cause = exception as? Exception),
            )
        )
    }

    override fun onProgress(context: Context, uploadInfo: UploadInfo) {
        emit(
            UploadStatusSource.State.Progress(
                uploadId = uploadInfo.uploadId,
                progress = uploadInfo.progressPercent,
            )
        )
    }

    override fun onSuccess(
        context: Context,
        uploadInfo: UploadInfo,
        serverResponse: ServerResponse
    ) {
        emit(UploadStatusSource.State.Success(uploadId = uploadInfo.uploadId))
    }

    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {
    }

    override fun onCompletedWhileNotObserving() {
    }
}