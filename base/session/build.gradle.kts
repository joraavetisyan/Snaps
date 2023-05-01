plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.basesession"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseProfile)
    implementation(projects.baseSources)
    implementation(projects.baseAuth)
    implementation(projects.baseWallet)
    implementation(projects.baseNft)

    implementation(*Libs.bundle.firebase)
    implementation(*Libs.bundle.preference)
    implementation(*Libs.bundle.datastore)
}