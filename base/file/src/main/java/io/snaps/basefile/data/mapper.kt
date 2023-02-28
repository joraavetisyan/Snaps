package io.snaps.basefile.data

import io.snaps.basefile.data.model.UploadFileResponseDto
import io.snaps.basefile.domain.FileModel

fun UploadFileResponseDto.toFileModel() = FileModel(
    id = fileId,
    name = fileName,
)