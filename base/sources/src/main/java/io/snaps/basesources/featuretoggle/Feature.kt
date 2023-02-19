package io.snaps.basesources.featuretoggle

enum class Feature(
    val key: String = "",
    val defaultValue: Boolean = true,
) {
    ProfileApiMock("ProfileApiMock", true),
    FeedApiMock("FeedApiMock", true),
    CommentApiMock("CommentApiMock", true),
    SubsApiMock("SubsApiMock", true),
    WalletApiMock("WalletApiMock", true),
    TasksApiMock("TasksApiMock", true),
    MyCollectionApiMock("MyCollectionApiMock", true),
    UNKNOWN
}