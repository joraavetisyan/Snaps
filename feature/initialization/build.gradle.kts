plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.featureinitialization"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseProfile)
    implementation(projects.baseFile)
    implementation(projects.baseWallet)
    implementation(projects.baseSession)
    implementation(projects.baseSettings)
    implementation(projects.baseSources)
}