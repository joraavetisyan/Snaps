plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.featurecollection"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseProfile)
    implementation(projects.baseSources)
    implementation(projects.baseBilling)
    implementation(projects.baseWallet)
    implementation(projects.baseNft)
    implementation(projects.baseSession)
}