package io.snaps.baseprofile.data

import dagger.Lazy
import io.snaps.baseprofile.data.model.ConnectInstagramRequestDto
import io.snaps.baseprofile.data.model.EditUserRequestDto
import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.domain.InvitedReferralModel
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.domain.UsersPageModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.coroutine.UserSessionCoroutineScope
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
import javax.inject.Inject

interface ProfileRepository {

    val state: StateFlow<State<UserInfoModel>>

    val currentTasksState: StateFlow<State<QuestInfoModel>>

    val invitedFirstReferralState: StateFlow<State<InvitedReferralModel>>

    val invitedSecondReferralState: StateFlow<State<InvitedReferralModel>>

    fun getUsersState(query: String): StateFlow<UsersPageModel>

    suspend fun refreshUsers(query: String): Effect<Completable>

    suspend fun loadNextUsersPage(query: String): Effect<Completable>

    suspend fun updateData(
        isSilently: Boolean = false,
    ): Effect<UserInfoModel>

    suspend fun getUserInfoById(userId: String): Effect<UserInfoModel>

    suspend fun editUser(
        avatar: FullUrl?,
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

    suspend fun updateInvitedFirstReferral(): Effect<Completable>

    suspend fun updateInvitedSecondReferral(): Effect<Completable>
}

class ProfileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @UserSessionCoroutineScope private val scope: CoroutineScope,
    private val api: Lazy<ProfileApi>,
    private val loaderFactory: UsersLoaderFactory,
) : ProfileRepository {

    private val _state = MutableStateFlow<State<UserInfoModel>>(Loading())
    override val state = _state.asStateFlow()

    private val _invitedFirstReferralState = MutableStateFlow<State<InvitedReferralModel>>(Loading())
    override val invitedFirstReferralState = _invitedFirstReferralState.asStateFlow()

    private val _invitedSecondReferralState = MutableStateFlow<State<InvitedReferralModel>>(Loading())
    override val invitedSecondReferralState = _invitedSecondReferralState.asStateFlow()

    override val currentTasksState = state.map {
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
                    api.get().users(
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

    // todo if fast switched between screens which update this state, we end up with Loading on other screens,
    // as the request gets cancelled, use cache?
    override suspend fun updateData(isSilently: Boolean): Effect<UserInfoModel> {
        if (!isSilently) {
            _state tryPublish Loading()
        }
        return apiCall(ioDispatcher) {
            api.get().userInfo()
        }.map {
            it.toModel()
        }.also {
            _state tryPublish it
        }
    }

    override suspend fun getUserInfoById(userId: String): Effect<UserInfoModel> {
        return apiCall(ioDispatcher) {
            api.get().userInfo(userId)
        }.map {
            it.toModel()
        }
    }

    override suspend fun editUser(
        avatar: FullUrl?,
        userName: String,
        address: CryptoAddress
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.get().editUser(
                EditUserRequestDto(
                    name = userName,
                    avatarUrl = avatar,
                    wallet = address,
                )
            )
        }.map {
            it.toModel().copy(questInfo = state.value.dataOrCache?.questInfo) // user model without questInfo is returned
        }.also {
            _state tryPublish it
        }.toCompletable()
    }

    override suspend fun setInviteCode(inviteCode: String): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.get().setInviteCode(
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
        }.toCompletable()
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
            api.get().connectInstagram(
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
            api.get().connectInstagram(
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

    override suspend fun updateInvitedFirstReferral(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.get().getInvitedFirstReferral()
        }.map {
            it.toModel()
        }.also {
            _invitedFirstReferralState tryPublish it
        }.toCompletable()
    }

    override suspend fun updateInvitedSecondReferral(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.get().getInvitedSecondReferral()
        }.map {
            it.toModel()
        }.also {
            _invitedSecondReferralState tryPublish it
        }.toCompletable()
    }
}