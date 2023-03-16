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

    implementation(projects.baseSession)
    implementation(projects.baseSources)
    implementation(projects.baseAuth)

    implementation(*Libs.bundle.firebase)
    implementation(Libs.bundle.playServices)
    implementation(*Libs.bundle.facebook)
}