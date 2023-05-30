package io.snaps.basesources.featuretoggle

private const val allMocks = false

enum class Feature(
    val key: String = "",
    val defaultValue: Boolean,
    val isRemote: Boolean,
    val isVersionChecked: Boolean,
) {
    // Local only
    // Mocks
    ProfileApiMock("ProfileApiMock", allMocks, false, false),
    FeedApiMock("FeedApiMock", allMocks, false, false),
    CommentApiMock("CommentApiMock", allMocks, false, false),
    SubsApiMock("SubsApiMock", allMocks, false, false),
    WalletApiMock("WalletApiMock", allMocks, false, false),
    TasksApiMock("TasksApiMock", allMocks, false, false),
    NftApiMock("NftApiMock", allMocks, false, false),

    // Remote
    PurchaseNftWithBnb("nft_bnb_enabled", false, true, true),
    SellSnaps("snaps_cell_enabled", false, true, true),
    Captcha("captcha_enabled", false, true, false),
}