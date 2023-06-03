plugins {
    id(Libs.plugin.library)
}

common()
dagger()
lifecycle()
firebase()

android {
    namespace = "io.snaps.basesources"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)

    implementation(*Libs.bundle.preference)
}