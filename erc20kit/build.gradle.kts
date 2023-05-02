plugins {
    id(Libs.plugin.library)
}

common()

android {
    namespace = "io.horizontalsystems.erc20kit"
}

dependencies {
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-rxjava2:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")

    implementation(projects.ethereumkit)
}