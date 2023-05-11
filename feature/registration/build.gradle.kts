plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()
firebase()

android {
    namespace = "io.snaps.featureregistration"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseSession)
    implementation(projects.baseSources)
    implementation(projects.baseAuth)
    implementation(projects.baseProfile)

    implementation(Libs.bundle.playServices)
    implementation(*Libs.bundle.facebook)
}