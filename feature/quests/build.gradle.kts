plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.featurequests"
}

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
    implementation(projects.baseFeed)
    implementation(projects.baseQuests)

    implementation(*Libs.bundle.gson)
}