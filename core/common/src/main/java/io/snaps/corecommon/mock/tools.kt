package io.snaps.corecommon.mock

import kotlin.random.Random

val rVideos = listOf(
    "https://user-images.githubusercontent.com/90382113/170887700-e405c71e-fe31-458d-8572-aea2e801eecc.mp4",
    "https://user-images.githubusercontent.com/90382113/170885742-d82e3b59-e45a-4fcf-a851-fed58ff5a690.mp4",
    "https://user-images.githubusercontent.com/90382113/170888784-5d9a7623-10c8-4ca2-8585-fdb29b2ed037.mp4",
    "https://user-images.githubusercontent.com/90382113/170889265-7ed9a56c-dd5f-4d78-b453-18b011644da0.mp4",
    "https://user-images.githubusercontent.com/90382113/170890384-43214cc8-79c6-4815-bcb7-e22f6f7fe1bc.mp4",
)

val rImage get() = "https://picsum.photos/64/64"

const val mockDelay = 2000L

val rInt get() = Random.nextInt(from = 0, until = 200000)
val rBool get() = Random.nextBoolean()
val rDouble get() = Random.nextDouble(0.0, 1.0)