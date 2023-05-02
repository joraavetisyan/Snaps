plugins {
    id(Libs.plugin.library)
}

common()
dagger()
room()

android {
    namespace = "io.snaps.corecrypto"
}

dependencies {
    api(*Libs.bundle.requests)
    api(*Libs.bundle.kotlinSerialization)

    implementation(projects.ethereumkit)
    implementation(projects.erc20kit)
    implementation(projects.binancechainkit)

    implementation(projects.coreCommon)

    implementation(*Libs.bundle.datastore)
    implementation(*Libs.bundle.preference)
    implementation(*Libs.bundle.cryptoPreference)
    implementation(*Libs.bundle.biometric)
    implementation(*Libs.bundle.crypto)
    implementation(*Libs.bundle.gson)
}