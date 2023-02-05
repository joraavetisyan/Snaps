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
    implementation(projects.baseSession)

    implementation(projects.featureFeed)
    implementation(projects.featureTasks)
    implementation(projects.featureCollection)
    implementation(projects.featureProfile)
}