plugins {
    id(Libs.plugin.library)
}

common()
dagger()
compose()
firebase()

android {
    namespace = "io.snaps.corenavigation"
}

dependencies {
    api(*Libs.bundle.navigation)
    api(*Libs.bundle.kotlinSerialization)

    implementation(projects.coreCommon)

    implementation(*Libs.bundle.browser)
}