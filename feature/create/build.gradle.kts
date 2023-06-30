plugins {
    id(Libs.plugin.library)
    id(Libs.plugin.imgly) version Libs.imglyVersion
}

common()
compose()
dagger()
lifecycle()

android {
    namespace = "io.snaps.featurecreate"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseProfile)
    implementation(projects.baseSources)
    implementation(projects.basePlayer)
    implementation(projects.baseFile)
    implementation(projects.baseFeed)

    implementation(*Libs.bundle.camerax)
    implementation(*Libs.bundle.videoCompressor)
}

imglyConfig {
    // https://img.ly/docs/vesdk/android/getting-started/integration/

    vesdk {
        finalModule = true
        enabled(true)
        licensePath(
            // todo release
            // uncomment this for debug
            // null
            // uncomment this for release
            "vesdk_android_license"
        )
    }

    modules {
        include("ui:core")
        include("ui:text")
        include("ui:focus")
        include("ui:frame")
        include("ui:brush")
        include("ui:filter")
        include("ui:sticker")
        include("ui:overlay")
        include("ui:transform")
        include("ui:adjustment")
        include("ui:text-design")

        include("ui:video-trim")
        include("ui:video-library")
        include("ui:video-composition")
        // include("ui:audio-composition")
        include("ui:giphy-sticker")

        include("backend:serializer")
        include("backend:headless")
        include("backend:background-removal")
        include("backend:sticker-smart")
        include("backend:sticker-animated")

        include("assets:font-basic")
        include("assets:frame-basic")
        include("assets:filter-basic")
        include("assets:overlay-basic")
        include("assets:sticker-shapes")
        include("assets:sticker-emoticons")
    }
}