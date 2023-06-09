plugins {
    id(Libs.plugin.library)
}

common()
dagger()
lifecycle()

android {
    namespace = "io.snaps.basesettings"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)

    implementation(projects.baseSources)
}