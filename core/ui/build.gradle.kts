plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.coreui"
}

dependencies {
    api(projects.coreUiCompose)
    implementation(projects.coreCommon)

    implementation(*Libs.bundle.splashscreen)
    implementation(*Libs.bundle.biometric)
    implementation(Libs.bundle.barcode)
}