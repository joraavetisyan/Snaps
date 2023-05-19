plugins {
    id(Libs.plugin.library)
}

common()
dagger()
lifecycle()
firebase()

android {
    namespace = "io.snaps.basebilling"
}

dependencies {
    api(*Libs.bundle.billing)

    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
}