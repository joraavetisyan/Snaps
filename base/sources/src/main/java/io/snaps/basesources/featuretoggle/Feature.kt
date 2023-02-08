package io.snaps.basesources.featuretoggle

enum class Feature(
    val key: String = "",
    val defaultValue: Boolean = true,
) {
    ProfileApiMock("ProfileApiMock", true),
    FeedApiMock("FeedApiMock", true),
    UNKNOWN
}