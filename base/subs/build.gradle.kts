plugins {
    id(Libs.plugin.library)
}

common()
dagger()
lifecycle()

android {
    namespace = "io.snaps.basesubs"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)

    implementation(projects.baseSources)
    implementation(projects.baseProfile)
}