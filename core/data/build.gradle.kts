plugins {
    id(Libs.plugin.library)
}

common()
lifecycle()
dagger()
room()
firebase()

android {
    namespace = "io.snaps.coredata"
}

dependencies {
    api(*Libs.bundle.requests)
    api(*Libs.bundle.kotlinSerialization)

    implementation(projects.coreCommon)

    implementation(*Libs.bundle.datastore)
    implementation(*Libs.bundle.preference)
    implementation(*Libs.bundle.cryptoPreference)
    implementation(*Libs.bundle.biometric)
}