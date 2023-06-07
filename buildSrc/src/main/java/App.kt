object App {

    const val minSdk = 26
    const val targetSdk = 33
    const val compileSdk = 33
    const val packageName = "io.snaps.android"

    val code
        get() = version

    val name
        get() = "$major.$minor.$patch"

    private var version = 26

    private var major = 1
    private var minor = 0
    private var patch = 13
}