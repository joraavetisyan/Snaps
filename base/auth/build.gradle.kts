plugins {
    id(Libs.plugin.library)
}

common()
dagger()
lifecycle()
firebase()

android {
    namespace = "io.snaps.baseauth"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
}