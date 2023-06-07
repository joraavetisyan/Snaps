package io.snaps.basesources.remotedata

import io.snaps.basesources.remotedata.model.BannerDto
import io.snaps.basesources.remotedata.model.SocialPageDto
import io.snaps.corecommon.model.Effect

interface RemoteDataProvider {

    suspend fun getBanner(): Effect<BannerDto>

    suspend fun getSocialPages(): Effect<List<SocialPageDto>>
}