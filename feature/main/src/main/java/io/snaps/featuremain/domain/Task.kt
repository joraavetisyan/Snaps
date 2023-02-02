package io.snaps.featuremain.domain

import io.snaps.corecommon.model.Uuid

data class Task(
    val id: Uuid,
    val title: String,
    val description: String,
    val result: String,
    val isCompleted: Boolean,
    val type: Type,
) {

    enum class Type {
        WatchVideo,
        LikeAndSubscribe,
        Share,
        FindPoints,
        ;
    }
}