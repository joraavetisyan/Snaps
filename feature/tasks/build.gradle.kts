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

    implementation(projects.baseProfile)
    implementation(projects.baseSources)
    implementation(projects.baseNft)
    implementation(projects.baseSession)
    implementation(projects.baseWallet)

    implementation(*Libs.bundle.gson)
}