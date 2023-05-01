plugins {
    id(Libs.plugin.library)
}

common()
dagger()
lifecycle()

android {
    namespace = "io.snaps.basefile"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
}