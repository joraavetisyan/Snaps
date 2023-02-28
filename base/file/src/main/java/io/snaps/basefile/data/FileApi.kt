package io.snaps.basefile.data

import io.snaps.basefile.data.model.UploadFileResponseDto
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Streaming

interface FileApi {

    @Multipart
    @POST("file")
    suspend fun upload(
        @Part file: MultipartBody.Part,
    ): BaseResponse<UploadFileResponseDto>

    @Streaming
    @GET("file")
    suspend fun download(
        @Query("fileId") fileId: Uuid,
    ): BaseResponse<ResponseBody>
}