package io.snaps.basesubs.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid

data class SubModel(
    val entityId: Uuid,
    val userId: Uuid,
    val avatar: ImageValue?,
    val name: String,
    val isSubscribed: Boolean?, // null means that we couldn't get the subscribe status todo better way
)