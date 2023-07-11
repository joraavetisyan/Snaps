plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.featureprofile"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseSession)
    implementation(projects.baseProfile)
    implementation(projects.baseFeed)
    implementation(projects.basePlayer)
    implementation(projects.baseSources)
    implementation(projects.baseWallet)
    implementation(projects.baseSubs)
    implementation(projects.baseFile)
    implementation(projects.baseSettings)
    implementation(projects.baseNotifications)
}