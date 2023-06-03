package io.snaps.basefeed.data

import android.content.Context
import androidx.work.WorkManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.network.ApiService
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.FileManager
import kotlinx.coroutines.CoroutineDispatcher
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import video.api.client.api.models.Environment
import video.api.client.api.work.stores.VideosApiStore
import video.api.client.api.work.upload
import java.io.File
import javax.inject.Inject

interface VideFeedUploader {

    /**
     * returns: upload id, to track the progress
     */
    suspend fun upload(videoId: Uuid, filePath: String): Effect<Uuid>
}

class VideFeedUploaderApivideoWorkManagerImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val buildInfo: BuildInfo,
) : VideFeedUploader {

    init {
        try {
            // todo central source for fb remotes
            FirebaseRemoteConfig.getInstance().getString(if (buildInfo.isRelease) "video_key" else "video_key_dev")
        } catch (e: Exception) {
            log(e, "VideosApiStore is not initialized due to api key fetch error!")
            null
        }?.let {
            log("VideosApiStore init with key $it")
            VideosApiStore.initialize(
                apiKey = it,
                environment = if (buildInfo.isRelease) Environment.PRODUCTION else Environment.PRODUCTION,
            )
        }
    }

    override suspend fun upload(videoId: Uuid, filePath: String): Effect<Uuid> {
        val workManager = WorkManager.getInstance(applicationContext)
        val workId = workManager.upload(videoId, filePath).request.stringId
        return Effect.success(workId)
    }
}

// todo delete
class VideFeedUploaderGotevUploadServiceImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val buildInfo: BuildInfo,
    private val tokenStorage: TokenStorage,
) : VideFeedUploader {

    override suspend fun upload(videoId: Uuid, filePath: String): Effect<Uuid> {
        return try {
            val uploadId = MultipartUploadRequest(
                context = applicationContext,
                serverUrl = "${ApiService.General.getBaseUrl(buildInfo)}v1/${videoId}/upload",
            ).apply {
                setMethod("POST")
                addHeader("Authorization", "${tokenStorage.authToken}")
                addFileToUpload(
                    filePath = filePath,
                    parameterName = "videoFile",
                )
            }.startUpload()
            Effect.success(uploadId)
        } catch (e: Exception) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }
}

// todo delete
class VideoFeedUploaderApiImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val videoFeedApi: VideoFeedApi,
    private val fileManager: FileManager,
) : VideFeedUploader {

    /**
     * Cannot track the progress with that
     */
    override suspend fun upload(videoId: Uuid, filePath: String): Effect<Uuid> {
        val videoFile = File(filePath)
        val mediaType = fileManager.getMimeType(videoFile.path) ?: MultipartBody.FORM
        val multipartBody = MultipartBody.Part.createFormData(
            name = "videoFile",
            filename = videoFile.name,
            body = videoFile.asRequestBody(mediaType),
        )
        return apiCall(ioDispatcher) {
            videoFeedApi.uploadVideo(file = multipartBody, videoId = videoId)
        }.map { "" }
    }
}