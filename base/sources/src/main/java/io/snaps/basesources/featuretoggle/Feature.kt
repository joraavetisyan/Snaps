package io.snaps.basesources.featuretoggle

enum class Feature(
    val key: String = "",
    val defaultValue: Boolean = true,
) {
    AuthApiMock("AuthApiMock", true),
    ProfileApiMock("ProfileApiMock", true),
    FeedApiMock("FeedApiMock", false),
    CommentApiMock("CommentApiMock", false),
    SubsApiMock("SubsApiMock", false),
    WalletApiMock("WalletApiMock", false),
    TasksApiMock("TasksApiMock", false),
    MyCollectionApiMock("MyCollectionApiMock", true),
    TransactionsApiMock("TransactionsApiMock", true),
    UNKNOWN
}