package io.snaps.basesources

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.GlobalRequestObserver
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import javax.inject.Inject
import javax.inject.Singleton

interface UploadStatusSource {

    val state: StateFlow<State?>

    fun listenTo(uploadId: Uuid): Flow<State?>

    sealed class State {
        abstract val uploadId: Uuid

        data class Progress(override val uploadId: Uuid, val progress: Int) : State()
        data class Success(override val uploadId: Uuid) : State()
        data class Error(override val uploadId: Uuid, val error: AppError) : State()
    }
}

class UploadStatusSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationCoroutineScope private val coroutineScope: CoroutineScope,
) : UploadStatusSource, RequestObserverDelegate {

    private val _state = MutableStateFlow<UploadStatusSource.State?>(null)
    override val state = _state.asStateFlow()

    init {
        GlobalRequestObserver(context as Application, this)
    }

    override fun listenTo(uploadId: Uuid): Flow<UploadStatusSource.State?> {
        return state.filter {
            it?.uploadId == uploadId
        }
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

@Module
@InstallIn(SingletonComponent::class)
interface UploadStatusSourceModule {

    @Binds
    @Singleton
    fun UploadStatusSource(source: UploadStatusSourceImpl): UploadStatusSource
}