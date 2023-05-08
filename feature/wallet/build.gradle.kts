plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.featurewallet"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseSources)
    implementation(projects.baseProfile)
    implementation(projects.baseWallet)
    implementation(projects.baseSession)
    implementation(projects.baseNft)
}