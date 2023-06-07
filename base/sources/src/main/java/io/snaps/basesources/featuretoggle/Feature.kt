package io.snaps.basesources.featuretoggle

import io.snaps.basesources.BuildConfig.DEBUG

private const val allMocks = false

enum class Feature(
    val key: String = "",
    val defaultValue: Boolean,
    val isRemote: Boolean,
    val isVersionChecked: Boolean,
) {
    // Local only
    // Mocks
    ProfileApiMock("ProfileApiMock", DEBUG && allMocks, false, false),
    FeedApiMock("FeedApiMock", DEBUG && allMocks, false, false),
    CommentApiMock("CommentApiMock", DEBUG && allMocks, false, false),
    SubsApiMock("SubsApiMock", DEBUG && allMocks, false, false),
    WalletApiMock("WalletApiMock", DEBUG && allMocks, false, false),
    TasksApiMock("TasksApiMock", DEBUG && allMocks, false, false),
    NftApiMock("NftApiMock", DEBUG && allMocks, false, false),
    RemoteDataProviderMock("RemoteDataProviderMock", DEBUG && allMocks, false, false),

    // Remote
    PurchaseNftWithBnb("nft_bnb_enabled", false, true, true),
    PurchaseNftInStore("android_buy_button", false, true, true),
    SellSnaps("snaps_cell_enabled", false, true, true),
    Captcha("captcha_enabled", false, true, false),
}