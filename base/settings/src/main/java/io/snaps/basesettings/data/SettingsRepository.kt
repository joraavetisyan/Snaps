package io.snaps.basesettings.data

import io.snaps.basesettings.domain.CommonSettingsModel
import io.snaps.basesources.featuretoggle.EditableFeatureToggle
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesettings.data.model.BannerDto
import io.snaps.basesettings.data.model.SettingsDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
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
import javax.inject.Inject

interface SettingsRepository {

    val state: StateFlow<State<SettingsDto>>

    val bannerState: StateFlow<State<BannerDto>>

    suspend fun update(): Effect<Completable>

    suspend fun getCommonSettings(): Effect<CommonSettingsModel>

    suspend fun getVideoApiKey(): Effect<String>
}

// todo return domain data
class SettingsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val api: SettingsApi,
    private val buildInfo: BuildInfo,
    private val featureToggle: EditableFeatureToggle,
    private val userDataStorage: UserDataStorage,
) : SettingsRepository {

    private val _state = MutableStateFlow<State<SettingsDto>>(Loading())
    override val state = _state.asStateFlow()

    override val bannerState = state.map {
        when (it) {
            is Loading -> Loading()
            is Effect -> when {
                it.isSuccess -> Effect.success(
                    requireNotNull(it.requireData.banner)
                )
                else -> Effect.error(requireNotNull(it.errorOrNull))
            }
        }
    }.likeStateFlow(scope, Loading())

    override suspend fun update(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.settings()
        }.doOnSuccess {
            it.setRemotes()
            it.banner.checkBannerVersion()
        }.also {
            _state tryPublish it
        }.toCompletable()
    }

    override suspend fun getCommonSettings(): Effect<CommonSettingsModel> {
        return apiCall(ioDispatcher) {
            api.commonSettings()
        }.map {
            it.toModel()
        }
    }

    override suspend fun getVideoApiKey(): Effect<String> {
        return state.value.dataOrCache?.let {
            Effect.success(it.mapToVideoApiKey())
        } ?: apiCall(ioDispatcher) {
            api.settings()
        }.map { it.mapToVideoApiKey() }
    }

    private fun SettingsDto.mapToVideoApiKey(): String {
        return if (buildInfo.isRelease) videoKey else videoKeyDev
    }

    private fun SettingsDto.setRemotes() {
        featureToggle.clearRemoteValues()
        val currentVersion = buildInfo.versionCode
        val remoteApplyVersion = this.togglesVersion
        Feature.values().filter(Feature::isRemote).forEach { feature ->
            val isFetchable = !feature.isVersionChecked || currentVersion != remoteApplyVersion
            val value = when (feature.key) {
                Feature.PurchaseNftWithBnb.key -> this.purchaseNftWithBnb
                Feature.PurchaseNftInStore.key -> this.purchaseNftInStore
                Feature.Captcha.key -> this.captcha
                Feature.SellSnaps.key -> this.sellSnaps
                else -> false
            }
            featureToggle.setRemoteValue(
                feature = feature,
                value = if (isFetchable) value.also {
                    log("Fetched config $feature: $it")
                } else feature.defaultValue,
            )
        }
    }

    private fun BannerDto.checkBannerVersion() {
        if (userDataStorage.bannerVersion < this.version) {
            userDataStorage.bannerVersion = this.version
            userDataStorage.countBannerViews = 0
        }
    }
}