plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.basefeed"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreNavigation)
    implementation(projects.coreData)
    implementation(projects.coreUi)

    implementation(projects.baseSources)
    implementation(projects.basePlayer)
    implementation(projects.baseProfile)
    implementation(projects.baseSubs)

    implementation(*Libs.bundle.work)
    implementation(*Libs.bundle.media)
    implementation(*Libs.bundle.uploadService)
    implementation(*Libs.bundle.apivideo)
    implementation(*Libs.bundle.firebase)
}