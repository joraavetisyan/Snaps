package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.ConnectInstagramRequestDto
import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.data.model.UserCreateRequestDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.BalanceModel
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.domain.UsersPageModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
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

    val balanceState: StateFlow<State<BalanceModel>>

    val currentQuestsState: StateFlow<State<QuestInfoModel>>

    val referralsState: StateFlow<State<List<UserInfoModel>>>

    fun getUsersState(query: String): StateFlow<UsersPageModel>

    suspend fun refreshUsers(query: String): Effect<Completable>

    suspend fun loadNextUsersPage(query: String): Effect<Completable>

    suspend fun updateData(): Effect<UserInfoModel>

    suspend fun updateBalance(): Effect<Completable>

    suspend fun updateReferrals(): Effect<Completable>

    suspend fun getUserInfoById(userId: String): Effect<UserInfoModel>

    suspend fun createUser(
        fileId: Uuid,
        userName: String,
        walletAddress: WalletAddress,
    ): Effect<Completable>

    suspend fun setInviteCode(inviteCode: String): Effect<Completable>

    fun isCurrentUser(userId: Uuid): Boolean

    suspend fun connectInstagram(
        instagramId: String,
        instagramUsername: String,
        name: String,
        walletAddress: WalletAddress,
        avatar: FullUrl,
    ): Effect<Completable>

    suspend fun disconnectInstagram(
        name: String,
        walletAddress: WalletAddress,
        avatar: FullUrl,
    ): Effect<Completable>
}

class ProfileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val api: ProfileApi,
    private val userDataStorage: UserDataStorage,
    private val loaderFactory: UsersLoaderFactory,
) : ProfileRepository {

    private val _state = MutableStateFlow<State<UserInfoModel>>(Loading())
    override val state = _state.asStateFlow()

    private val _balanceState = MutableStateFlow<State<BalanceModel>>(Loading())
    override val balanceState = _balanceState.asStateFlow()

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
                pageSize = 20,
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

    override suspend fun updateData(): Effect<UserInfoModel> {
        _state tryPublish Loading()
        return apiCall(ioDispatcher) {
            api.userInfo()
        }.map {
            it.toModel()
        }.also {
            _state tryPublish it
        }
    }

    override suspend fun updateBalance(): Effect<Completable> {
        _balanceState tryPublish Loading()
        return apiCall(ioDispatcher) {
            api.balance()
        }.map {
            it.toModel()
        }.also {
            _balanceState tryPublish it
        }.toCompletable()
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
        walletAddress: WalletAddress,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.createUser(
                UserCreateRequestDto(
                    name = userName,
                    avatarUrl = "http://51.250.36.197:5100/api/v1/file?fileId=$fileId", // todo
                    wallet = walletAddress,
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
        instagramId: String,
        instagramUsername: String,
        name: String,
        walletAddress: WalletAddress,
        avatar: FullUrl,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.connectInstagram(
                ConnectInstagramRequestDto(
                    instagramId = instagramId,
                    wallet = walletAddress,
                    name = name,
                    avatarUrl = avatar,
                )
            )
        }.doOnSuccess {
            userDataStorage.instagramUsername = instagramUsername
            _state.update {
                if (it is Effect && it.isSuccess) {
                    Effect.success(it.requireData.copy(instagramId = instagramId))
                } else {
                    it
                }
            }
        }.toCompletable()
    }

    override suspend fun disconnectInstagram(
        name: String,
        walletAddress: WalletAddress,
        avatar: FullUrl,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.connectInstagram(
                ConnectInstagramRequestDto(
                    instagramId = null,
                    wallet = walletAddress,
                    name = name,
                    avatarUrl = avatar,
                )
            )
        }.doOnSuccess {
            userDataStorage.instagramUsername = ""
            _state.update {
                if (it is Effect && it.isSuccess) {
                    Effect.success(it.requireData.copy(instagramId = null))
                } else {
                    it
                }
            }
        }.toCompletable()
    }
}