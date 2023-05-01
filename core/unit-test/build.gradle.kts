plugins {
    id(Libs.plugin.library)
}

common()

android {
    namespace = "io.snaps.coreunittest"
}

dependencies {
    api(*Libs.bundle.unitTests)

    implementation(projects.coreCommon)
}