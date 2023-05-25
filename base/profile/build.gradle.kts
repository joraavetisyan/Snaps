plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()
firebase()

android {
    namespace = "io.snaps.baseprofile"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseSources)
    implementation(projects.baseWallet)
    implementation(projects.baseNft)
}