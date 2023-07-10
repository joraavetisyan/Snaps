object App {

    const val minSdk = 26
    const val targetSdk = 33
    const val compileSdk = 33
    const val packageName = "io.snaps.android"

    val code
        get() = version

    val name
        get() = "$major.$minor.$patch"

    private var version = 44

    private var major = 1
    private var minor = 1
    private var patch = 0
}