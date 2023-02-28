package io.snaps.basefile.domain

import io.snaps.corecommon.model.Uuid

data class FileModel(
    val name: String,
    val id: Uuid,
)