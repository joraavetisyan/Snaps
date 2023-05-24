plugins {
    id(Libs.plugin.application)
    id(Libs.plugin.google_services)
    id(Libs.plugin.crashlytics)
    id(Libs.plugin.spotify_ruler)
    id(Libs.plugin.ben_manes_versions)
}

common()
compose()
dagger()
lifecycle()
firebase()

android {
    namespace = "io.snaps.android"

    defaultConfig {
        versionName = App.name
        versionCode = App.code
        applicationId = App.packageName

        configurations {
            all {
                exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
                exclude(group = "org.bouncycastle", module = "jetified-bcprov-jdk15on-1.64")
                exclude(group = "com.google.protobuf", module = "protobuf-javalite")
            }
        }
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

            isMinifyEnabled = false // todo release
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
            isMinifyEnabled = false // todo release
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
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
    implementation(projects.baseWallet)

    implementation(projects.featureBottomBar)
    implementation(projects.featureRegistration)
    implementation(projects.featureInitialization)
    implementation(projects.featureWallet)
    implementation(projects.featureWalletConnect)
    implementation(projects.featureProfile)
    implementation(projects.featureCollection)
    implementation(projects.featureTasks)
    implementation(projects.featureFeed)
    implementation(projects.featureSearch)
    implementation(projects.featureCreate)
    implementation(projects.featureReferral)
    implementation(projects.featureWebview)

    implementation(*Libs.bundle.splashscreen)
    implementation(*Libs.bundle.appUpdater)
    implementation(*Libs.bundle.biometric)
    implementation(Libs.bundle.playServicesAuth)
    implementation(*Libs.bundle.uploadService)
}