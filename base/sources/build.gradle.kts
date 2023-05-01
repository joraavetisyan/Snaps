plugins {
    id(Libs.plugin.library)
}

common()
dagger()
lifecycle()

android {
    namespace = "io.snaps.basesources"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)

    implementation(*Libs.bundle.preference)
    implementation(*Libs.bundle.firebase)
    implementation(Libs.bundle.uploadService)
}