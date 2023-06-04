package io.snaps.basefile.data

import io.snaps.basefile.domain.FileModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.FileManager
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import javax.inject.Inject

interface FileRepository {

    suspend fun uploadFile(file: File): Effect<FileModel>

    suspend fun downloadFile(fileId: Uuid): Effect<InputStream>
}

class FileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val api: FileApi,
    private val fileManager: FileManager,
) : FileRepository {

    override suspend fun uploadFile(file: File): Effect<FileModel> {
        val mediaType = fileManager.getMimeType(file.path) ?: MultipartBody.FORM

        val multipartBody = MultipartBody.Part.createFormData(
            name = "files",
            filename = file.name,
            body = file.asRequestBody(mediaType),
        )
        return apiCall(ioDispatcher) {
            api.upload(multipartBody)
        }.map {
            it.first().toFileModel()
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