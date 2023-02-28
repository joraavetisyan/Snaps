package io.snaps.basefile.data

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import io.snaps.basefile.domain.FileModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.InputStream
import javax.inject.Inject

interface FileRepository {

    suspend fun uploadFile(uri: Uri): Effect<FileModel>

    suspend fun downloadFile(fileId: Uuid): Effect<InputStream>
}

class FileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val api: FileApi,
) : FileRepository {

    override suspend fun uploadFile(uri: Uri): Effect<FileModel> {
        val file = uri.buildUpon().scheme("file").build().toFile()
        val mediaType = "multipart/form-data".toMediaType()

        val multipartBody = MultipartBody.Part.createFormData(
            name = "files",
            filename = file.name,
            body = file.asRequestBody(mediaType),
        )
        return apiCall(ioDispatcher) {
            api.upload(multipartBody)
        }.map {
            it.toFileModel()
        }.doOnError { error, data ->
            Log.e("errorUpload", error.code.toString())
            Log.e("errorUpload", error.message.toString())
        }
    }

    override suspend fun downloadFile(fileId: Uuid): Effect<InputStream> {
        return apiCall(ioDispatcher) {
            api.download(fileId)
        }.map {
           it.byteStream()
        }
    }
}