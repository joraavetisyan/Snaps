package io.snaps.baseprofile.data

import android.net.Uri
import androidx.core.net.toFile
import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.domain.CoinsModel
import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.likeStateFlow
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

interface ProfileRepository {

    // todo
    val coinState: StateFlow<State<CoinsModel>>

    val state: StateFlow<State<ProfileModel>>

    val currentQuestsState: StateFlow<State<List<QuestModel>>>

    suspend fun updateData(): Effect<Completable>

    suspend fun getUserInfoById(userId: String): Effect<ProfileModel>

    suspend fun createUser(uri: Uri, userName: String): Effect<Completable>

    suspend fun setInviteCode(inviteCode: String): Effect<Completable>

    fun isCurrentUser(userId: Uuid): Boolean
}

class ProfileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val api: ProfileApi,
    private val userDataStorage: UserDataStorage,
) : ProfileRepository {

    private val _state = MutableStateFlow<State<ProfileModel>>(Loading())
    override val state = _state.asStateFlow()

    private val _coinState = MutableStateFlow(
        Effect.success(CoinsModel(energy = "12", gold = "12", silver = "12", bronze = "12"))
    )
    override val coinState = _coinState.asStateFlow()

    override val currentQuestsState = state.map {
        when (it) {
            is Loading -> Loading()
            is Effect -> when {
                it.isSuccess -> Effect.success(it.requireData.quests)
                else -> Effect.error(requireNotNull(it.errorOrNull))
            }
        }
    }.likeStateFlow(scope, Loading())

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
            userDataStorage.hasNft = !it.hasNft
        }.map {
            it.toProfileModel()
        }.also {
            _state tryPublish it
        }.toCompletable()
    }

    override suspend fun setInviteCode(inviteCode: String): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.setInviteCode(
                SetInviteCodeRequestDto(inviteCode = inviteCode)
            )
        }.doOnSuccess {
            _state.update {
                if (it is Effect && it.isSuccess) {
                    Effect.success(it.requireData.copy(inviteCodeRegisteredBy = inviteCode))
                } else {
                    it
                }
            }
        }
    }

    override fun isCurrentUser(userId: Uuid): Boolean {
        return _state.value.dataOrCache?.userId == userId
    }
}