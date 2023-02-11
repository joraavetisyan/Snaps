package io.snaps.baseprofile.data

import android.net.Uri
import androidx.core.net.toFile
import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.FileType
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


interface ProfileRepository {

    val state: StateFlow<State<ProfileModel>>

    suspend fun updateData(): Effect<Completable>

    suspend fun getUserInfoById(userId: String): Effect<ProfileModel>

    suspend fun createUser(uri: Uri, userName: String): Effect<Completable>
}

class ProfileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val api: ProfileApi,
    private val userDataStorage: UserDataStorage,
) : ProfileRepository {

    private val _state = MutableStateFlow<State<ProfileModel>>(Loading())
    override val state = _state.asStateFlow()

    override suspend fun updateData(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.userInfo()
        }.map {
            it.toProfileModel()
        }.also {
            _state tryPublish it
        }.toCompletable()
    }

    override suspend fun getUserInfoById(userId: String): Effect<ProfileModel> {
        return apiCall(ioDispatcher) {
            api.userInfo(userId)
        }.map {
            it.toProfileModel()
        }
    }

    override suspend fun createUser(uri: Uri, userName: String): Effect<Completable> {
        val file = uri.toFile()
        val mediaType = "multipart/form-data".toMediaType()
        val name = userName.toRequestBody(mediaType)

        val multipartBody = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody(mediaType)
        )

        return apiCall(ioDispatcher) {
            api.createUser(multipartBody, name)
        }.doOnSuccess {
            userDataStorage.setUserName(it.name)
            userDataStorage.setUserAvatar(uri.toString())
        }.toCompletable()
    }
}