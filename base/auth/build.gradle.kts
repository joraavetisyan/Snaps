plugins {
    id(Libs.plugin.library)
}

common()
dagger()
lifecycle()

android {
    namespace = "io.snaps.baseauth"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)

    implementation(*Libs.bundle.firebase)
}