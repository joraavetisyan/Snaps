plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

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
}