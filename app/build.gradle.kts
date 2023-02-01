plugins {
    id(Libs.plugin.application)
    id(Libs.plugin.google_services)
    id(Libs.plugin.crashlytics)
    id(Libs.plugin.spotify_ruler)
}

common()
compose()
dagger()
lifecycle()

android {
    defaultConfig {
        versionName = App.name
        versionCode = App.code
        applicationId = App.packageName
    }

    ruler {
        abi.set("arm64-v8a")
        locale.set("en")
        screenDensity.set(480)
        sdkVersion.set(27)
    }

    signingConfigs {
        create(BuildTypes.release) {
            storeFile = rootProject.file(KeystoreParams.path)
            storePassword = KeystoreParams.storePassword
            keyAlias = KeystoreParams.keyAlias
            keyPassword = KeystoreParams.keyPassword
        }
    }

    buildTypes {
        applicationVariants.all {
            outputs.withType<com.android.build.gradle.api.ApkVariantOutput> {
                outputFileName = "${App.name}-$outputFileName"
            }
        }
        getByName(BuildTypes.debug) {
            versionNameSuffix = BuildTypes.debugSuffix
            applicationIdSuffix = BuildTypes.debugPackageSuffix

            addManifestPlaceholders(
                mapOf(
                    "app_name" to "SNAPS Debug",
                )
            )

            isMinifyEnabled = false
        }
        getByName(BuildTypes.alpha) {
            initWith(getByName(BuildTypes.debug))
            versionNameSuffix = BuildTypes.alphaSuffix
            applicationIdSuffix = BuildTypes.alphaPackageSuffix

            addManifestPlaceholders(
                mapOf(
                    "app_name" to "SNAPS Alpha",
                )
            )

            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName(BuildTypes.release) {
            versionNameSuffix = BuildTypes.releaseSuffix
            applicationIdSuffix = BuildTypes.releasePackageSuffix

            addManifestPlaceholders(
                mapOf(
                    "app_name" to "SNAPS",
                )
            )

            signingConfig = signingConfigs.getByName(BuildTypes.release)
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)

    implementation(projects.baseSources)
    implementation(projects.baseProfile)
    implementation(projects.baseSession)

    implementation(projects.featureBottomBar)
    implementation(projects.featureMain)
    implementation(projects.featureRegistration)
    implementation(projects.featureInitialisation)

    implementation(*Libs.bundle.splashscreen)
    implementation(*Libs.bundle.appUpdater)
    implementation(*Libs.bundle.firebase)
    implementation(Libs.bundle.playServices)
    implementation(*Libs.bundle.biometric)
}