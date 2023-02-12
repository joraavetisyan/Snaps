package io.snaps.baseprofile.data

import android.net.Uri
import androidx.core.net.toFile
import io.snaps.baseprofile.domain.CoinsModel
import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

interface ProfileRepository {

    // todo
    val coinState: StateFlow<State<CoinsModel>>

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

    private val _coinState = MutableStateFlow(
        Effect.success(CoinsModel(energy = "12", gold = "12", silver = "12", bronze = "12"))
    )
    override val coinState = _coinState.asStateFlow()

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
        // todo
        val file = uri.buildUpon().scheme("file").build().toFile()
        val mediaType = "multipart/form-data".toMediaType()
        val name = userName.toRequestBody(mediaType)

        val multipartBody = MultipartBody.Part.createFormData(
            name = "file",
            filename = file.name,
            body = file.asRequestBody(mediaType),
        )

        return apiCall(ioDispatcher) {
            api.createUser(file = multipartBody, userName = name)
        }.doOnSuccess {
            userDataStorage.setUserName(it.name)
            userDataStorage.setUserAvatar(uri.toString())
        }.toCompletable()
    }
}