package io.snaps.baseprofile.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.snaps.baseprofile.data.model.ConnectInstagramRequestDto
import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.data.model.SocialPage
import io.snaps.baseprofile.data.model.UserCreateRequestDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.domain.UsersPageModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.json.KotlinxSerializationJsonProvider
import io.snaps.coredata.network.PagedLoaderParams
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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

interface ProfileRepository {

    val state: StateFlow<State<UserInfoModel>>

    val currentQuestsState: StateFlow<State<QuestInfoModel>>

    val referralsState: StateFlow<State<List<UserInfoModel>>>

    fun getUsersState(query: String): StateFlow<UsersPageModel>

    suspend fun refreshUsers(query: String): Effect<Completable>

    suspend fun loadNextUsersPage(query: String): Effect<Completable>

    suspend fun updateData(
        isSilently: Boolean = false,
    ): Effect<UserInfoModel>

    suspend fun updateReferrals(): Effect<Completable>

    suspend fun getUserInfoById(userId: String): Effect<UserInfoModel>

    suspend fun createUser(
        fileId: Uuid,
        userName: String,
        address: CryptoAddress,
    ): Effect<Completable>

    suspend fun setInviteCode(inviteCode: String): Effect<Completable>

    fun isCurrentUser(userId: Uuid): Boolean

    suspend fun connectInstagram(
        instagramUsername: String,
        name: String,
        address: CryptoAddress,
        avatar: FullUrl?,
    ): Effect<Completable>

    suspend fun disconnectInstagram(
        name: String,
        address: CryptoAddress,
        avatar: FullUrl?,
    ): Effect<Completable>

    suspend fun getSocialPages(): Effect<List<SocialPage>>
}

class ProfileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val api: ProfileApi,
    private val loaderFactory: UsersLoaderFactory,
) : ProfileRepository {

    private val _state = MutableStateFlow<State<UserInfoModel>>(Loading())
    override val state = _state.asStateFlow()

    private val _referralsState = MutableStateFlow<State<List<UserInfoModel>>>(Loading())
    override val referralsState = _referralsState.asStateFlow()

    override val currentQuestsState = state.map {
        when (it) {
            is Loading -> Loading()
            is Effect -> when {
                it.isSuccess -> Effect.success(requireNotNull(it.requireData.questInfo))
                else -> Effect.error(requireNotNull(it.errorOrNull))
            }
        }
    }.likeStateFlow(scope, Loading())

    private fun getLoader(query: String): UsersLoader {
        return loaderFactory.get(query) {
            PagedLoaderParams(
                action = { from, count ->
                    api.users(
                        query = query,
                        from = from,
                        count = count,
                        onlyInvited = false,
                    )
                },
                pageSize = 100,
                nextPageIdFactory = { it.entityId },
                mapper = { it.toModelList() },
            )
        }
    }

    override fun getUsersState(query: String): StateFlow<UsersPageModel> = getLoader(query).state

    override suspend fun refreshUsers(query: String): Effect<Completable> =
        getLoader(query).refresh()

    override suspend fun loadNextUsersPage(query: String): Effect<Completable> =
        getLoader(query).loadNext()

    override suspend fun updateData(isSilently: Boolean): Effect<UserInfoModel> {
        if (!isSilently) {
            _state tryPublish Loading()
        }
        return apiCall(ioDispatcher) {
            api.userInfo()
        }.map {
            it.toModel()
        }.also {
            _state tryPublish it
        }
    }

    override suspend fun updateReferrals(): Effect<Completable> {
        _referralsState tryPublish Loading()
        return apiCall(ioDispatcher) {
            api.users(query = null, from = null, count = 100, onlyInvited = true)
        }.map {
            it.map(UserInfoResponseDto::toModel)
        }.also {
            _referralsState tryPublish it
        }.toCompletable()
    }

    override suspend fun getUserInfoById(userId: String): Effect<UserInfoModel> {
        return apiCall(ioDispatcher) {
            api.userInfo(userId)
        }.map {
            it.toModel()
        }
    }

    override suspend fun createUser(
        fileId: Uuid,
        userName: String,
        address: CryptoAddress,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.createUser(
                UserCreateRequestDto(
                    name = userName,
                    avatarUrl = "http://51.250.36.197:5100/api/v1/file?fileId=$fileId", // todo
                    wallet = address,
                )
            )
        }.map {
            it.toModel()
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

    override suspend fun connectInstagram(
        instagramUsername: String,
        name: String,
        address: CryptoAddress,
        avatar: FullUrl?,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.connectInstagram(
                ConnectInstagramRequestDto(
                    instagramId = instagramUsername,
                    wallet = address,
                    name = name,
                    avatarUrl = avatar,
                )
            )
        }.doOnSuccess {
            _state.update {
                if (it is Effect && it.isSuccess) {
                    Effect.success(it.requireData.copy(instagramId = instagramUsername))
                } else {
                    it
                }
            }
        }.toCompletable()
    }

    override suspend fun disconnectInstagram(
        name: String,
        address: CryptoAddress,
        avatar: FullUrl?,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.connectInstagram(
                ConnectInstagramRequestDto(
                    instagramId = null,
                    wallet = address,
                    name = name,
                    avatarUrl = avatar,
                )
            )
        }.doOnSuccess {
            _state.update {
                if (it is Effect && it.isSuccess) {
                    Effect.success(it.requireData.copy(instagramId = null))
                } else {
                    it
                }
            }
        }.toCompletable()
    }

    override suspend fun getSocialPages(): Effect<List<SocialPage>> {
         // fetch called in FeatureToggleUpdater, todo to separate source with proper success/failure handle
        val pages = try {
            FirebaseRemoteConfig.getInstance().getValue("social").let {
                @OptIn(ExperimentalSerializationApi::class)
                KotlinxSerializationJsonProvider().get().decodeFromStream<List<SocialPage>>(it.asByteArray().inputStream())
            }
        } catch (e: Exception) {
            log(e)
            emptyList()
        }
        return Effect.success(pages)
    }
}