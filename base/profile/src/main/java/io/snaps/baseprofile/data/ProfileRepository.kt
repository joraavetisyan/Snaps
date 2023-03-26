package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.data.model.UserCreateRequestDto
import io.snaps.baseprofile.domain.BalanceModel
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
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

    suspend fun updateData(): Effect<UserInfoModel>

    suspend fun updateBalance(): Effect<Completable>

    suspend fun getUserInfoById(userId: String): Effect<UserInfoModel>

    suspend fun createUser(
        fileId: Uuid,
        userName: String,
        walletAddress: WalletAddress,
    ): Effect<Completable>

    suspend fun setInviteCode(inviteCode: String): Effect<Completable>

    fun isCurrentUser(userId: Uuid): Boolean
}

class ProfileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val api: ProfileApi,
) : ProfileRepository {

    private val _state = MutableStateFlow<State<UserInfoModel>>(Loading())
    override val state = _state.asStateFlow()

    private val _balanceState = MutableStateFlow<State<BalanceModel>>(Loading())
    override val balanceState = _balanceState.asStateFlow()

    override val currentQuestsState = state.map {
        when (it) {
            is Loading -> Loading()
            is Effect -> when {
                it.isSuccess -> Effect.success(it.requireData.questInfo)
                else -> Effect.error(requireNotNull(it.errorOrNull))
            }
        }
    }.likeStateFlow(scope, Loading())

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
}