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

    implementation(projects.baseSources)
    implementation(projects.basePlayer)
    implementation(projects.baseProfile)

    implementation(*Libs.bundle.media)
    implementation(Libs.bundle.uploadService)
}