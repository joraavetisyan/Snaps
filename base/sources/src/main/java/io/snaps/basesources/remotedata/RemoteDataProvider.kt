package io.snaps.basesources.remotedata

import io.snaps.basesources.remotedata.model.AppUpdateInfoDto
import io.snaps.basesources.remotedata.model.BannerDto
import io.snaps.basesources.remotedata.model.SocialPageDto
import io.snaps.corecommon.model.Effect

// todo return domain models? use only in repos
// todo now SettingsApi is used instead
interface RemoteDataProvider {

    fun getBanner(): Effect<BannerDto>

    fun getSocialPages(): Effect<List<SocialPageDto>>

    fun getMaxVideoCount(): Effect<Int>

    fun getAppCurrentVersion(): Effect<AppUpdateInfoDto>

    fun getVideoapiKey(): Effect<String>
}