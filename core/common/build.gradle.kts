plugins {
    id(Libs.plugin.library)
}

common()
dagger()

dependencies {
    implementation(*Libs.bundle.kotlinSerialization)
    implementation(*Libs.bundle.firebase)

    // for previews to work properly
    // https://stackoverflow.com/questions/71812710/can-no-longer-view-jetpack-compose-previews-failed-to-instantiate-one-or-more-c
    debugApi("androidx.customview:customview:1.2.0-alpha02")
    debugApi("androidx.customview:customview-poolingcontainer:1.0.0")
}