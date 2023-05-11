package io.snaps.basesources.featuretoggle

enum class Feature(
    val key: String = "",
    val defaultValue: Boolean = true,
    val isRemote: Boolean,
) {
    // Mocks
    ProfileApiMock("ProfileApiMock", false, false),
    FeedApiMock("FeedApiMock", false, false),
    CommentApiMock("CommentApiMock", false, false),
    SubsApiMock("SubsApiMock", false, false),
    WalletApiMock("WalletApiMock", false, false),
    TasksApiMock("TasksApiMock", false, false),
    NftApiMock("NftApiMock", false, false),
    TransactionsApiMock("TransactionsApiMock", false, false), // todo use or remove

    // Ui elements
    PurchaseNftWithBnb("nft_bnb_enabled", false, true),
}