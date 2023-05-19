plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.featuresearch"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseSources)
    implementation(projects.baseProfile)
    implementation(projects.baseFeed)
    implementation(projects.basePlayer)
    implementation(projects.baseSession)
    implementation(projects.baseSubs)
}