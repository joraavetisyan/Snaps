package io.snaps.basesources.remotedata

import io.snaps.basesources.remotedata.model.BannerActionType
import io.snaps.basesources.remotedata.model.BannerDto
import io.snaps.basesources.remotedata.model.BannerTitleDto
import io.snaps.basesources.remotedata.model.SocialPageDto
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.model.Effect

class FakeRemoteDataProvider : RemoteDataProvider {

    override suspend fun getBanner(): Effect<BannerDto> {
        return Effect.success(
            BannerDto(
                isViewable = true,
                isEndless = true,
                version = 1,
                image = rImage,
                action = "",
                actionTitle = BannerTitleDto(
                    en = "Action",
                    ru = "Action",
                    tr = "Action",
                    uk = "Action",
                    es = "Action",
                ),
                title = BannerTitleDto(
                    en = "Title",
                    ru = "Title",
                    tr = "Title",
                    uk = "Title",
                    es = "Title",
                ),
                isTimerShown = true,
                actionType = BannerActionType.NftList,
            )
        )
    }

    override suspend fun getSocialPages(): Effect<List<SocialPageDto>> {
        return Effect.success(emptyList())
    }
}