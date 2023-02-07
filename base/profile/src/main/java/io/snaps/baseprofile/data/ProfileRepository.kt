package io.snaps.baseprofile.data

import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface ProfileRepository {

    val state: StateFlow<State<ProfileModel>>

    suspend fun updateData(): Effect<Completable>

    suspend fun getUserInfoById(userId: String): Effect<ProfileModel>
}

class ProfileRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val api: ProfileApi,
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
}