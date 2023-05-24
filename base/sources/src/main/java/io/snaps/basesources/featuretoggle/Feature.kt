package io.snaps.basesources.featuretoggle

private const val allMocks = false

enum class Feature(
    val key: String = "",
    val defaultValue: Boolean,
    val isRemote: Boolean,
) {
    // Local only
    // Mocks
    ProfileApiMock("ProfileApiMock", allMocks, false),
    FeedApiMock("FeedApiMock", allMocks, false),
    CommentApiMock("CommentApiMock", allMocks, false),
    SubsApiMock("SubsApiMock", allMocks, false),
    WalletApiMock("WalletApiMock", allMocks, false),
    TasksApiMock("TasksApiMock", allMocks, false),
    NftApiMock("NftApiMock", allMocks, false),

    // Remote
    PurchaseNftWithBnb("nft_bnb_enabled", false, true),
    SellSnaps("snaps_cell_enabled", false, true),
    Captcha("captcha_enabled", false, true),
}