package io.snaps.basesources.featuretoggle

enum class Feature(
    val key: String = "",
    val defaultValue: Boolean = true,
) {
    AuthApiMock("AuthApiMock", false),
    ProfileApiMock("ProfileApiMock", false),
    FeedApiMock("FeedApiMock", false),
    CommentApiMock("CommentApiMock", false),
    SubsApiMock("SubsApiMock", false),
    WalletApiMock("WalletApiMock", false),
    TasksApiMock("TasksApiMock", false),
    NftApiMock("NftApiMock", false),
    TransactionsApiMock("TransactionsApiMock", false),
    UNKNOWN
}