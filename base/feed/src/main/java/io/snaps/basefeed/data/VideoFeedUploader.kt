package io.snaps.basefeed.data

import android.content.Context
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.snaps.basesources.remotedata.RemoteDataProvider
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.logE
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import video.api.client.api.models.Environment
import video.api.client.api.work.stores.VideosApiStore
import video.api.client.api.work.upload
import javax.inject.Inject

interface VideoFeedUploader {

    /**
     * returns: upload id, to track the progress
     */
    suspend fun upload(videoId: Uuid, filePath: String): Effect<Uuid>
}

class VideoFeedUploaderApivideoWorkManagerImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    remoteDataProvider: RemoteDataProvider,
) : VideoFeedUploader {

    init {
        remoteDataProvider.getVideoapiKey().data.let {
            if (it == null) {
                logE("VideosApiStore is not initialized due to api key fetch error!")
            } else {
                log("VideosApiStore init with key $it")
                VideosApiStore.initialize(
                    apiKey = it,
                    environment = Environment.PRODUCTION, // no differentiation on purpose
                )
            }
        }
    }

    override suspend fun upload(videoId: Uuid, filePath: String): Effect<Uuid> {
        val workManager = WorkManager.getInstance(applicationContext)
        val workId = workManager.upload(videoId, filePath).request.stringId
        return Effect.success(workId)
    }
}