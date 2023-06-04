plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.featurecreate"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseProfile)
    implementation(projects.baseSources)
    implementation(projects.basePlayer)
    implementation(projects.baseFile)
    implementation(projects.baseFeed)

    implementation(*Libs.bundle.camerax)
    implementation(*Libs.bundle.videoCompressor)
}