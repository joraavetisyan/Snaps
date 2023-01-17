plugins {
    id(Libs.plugin.application)
}

common()
compose()
dagger()
lifecycle()

android {
    defaultConfig {
        versionName = "8.8.8"
        versionCode = 888
        applicationId = "com.defince.w2e.uikit"
    }

    buildTypes {
        all {
            addManifestPlaceholders(mapOf(
                "app_name" to "Watch2Earn UiKit",
            ))
        }
    }
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.featureWidget)

    implementation(*Libs.bundle.splashscreen)
}