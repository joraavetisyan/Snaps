object Libs {

    private const val gmsVersion = "4.3.15"
    private const val gradleVersion = "7.3.1"
    private const val desugarVersion = "1.1.5"
    private const val kotlinVersion = "1.8.21"
    private const val kotlinSerializationVersion = "1.3.0"
    private const val coroutinesVersion = "1.6.1"
    private const val kotlinterVersion = "3.8.0"
    private const val rulerVersion = "1.1.0"
    private const val benManesVersionsVersion = "0.45.0"

    /*Compatibility with Kotlin https://developer.android.com/jetpack/androidx/releases/compose-kotlin*/
    const val composeCompilerVersion = "1.4.7"

    /*https://developer.android.com/jetpack/androidx/releases/compose*/
    private const val composeVersion = "1.4.3"
    private const val composeFoundationVersion = "1.4.3"
    private const val composeMaterialVersion = "1.4.3"
    private const val composeMaterial3Version = "1.1.0"
    private const val composeGlanceVersion = "1.0.0-alpha03"
    private const val splashVersion = "1.0.0-beta01"
    private const val activityVersion = "1.4.0"
    private const val materialVersion = "1.7.0"
    private const val pagingVersion = "3.1.0"
    private const val accompanistVersion = "0.31.1-alpha"
    private const val coilVersion = "2.1.0"
    private const val lottieVersion = "6.0.0"

    private const val lifecycleVersion = "2.4.0"
    private const val navigationVersion = "2.6.0-rc02"
    private const val appUpdaterVersion = "1.8.1"
    private const val billingVersion = "4.0.0" // todo support "From 1 November 2023, all app updates must use Billing Library version 5 or newer."
    private const val browserVersion = "1.4.0"
    private const val biometricVersion = "1.2.0-alpha04"
    private const val windowVersion = "1.0.0"

    private const val hiltVersion = "2.45"
    private const val hiltJetpackVersion = "1.0.0"

    private const val crashlyticsGradleVersion = "2.5.2"
    private const val firebaseBomVersion = "32.0.0"

    private const val playServicesAuthVersion = "20.4.1"
    private const val recaptchaVersion = "18.1.1"

    private const val datastoreVersion = "1.0.0-rc01"
    private const val securityVersion = "1.1.0-alpha01" // Downgrading to 1.1.0-alpha01 solve issue InvalidProtocolBufferException
    private const val preferenceVersion = "1.1.1"
    private const val roomVersion = "2.5.1"
    private const val jsonConverterVersion = "0.8.0"
    private const val retrofitVersion = "2.9.0"
    private const val okHttpVersion = "4.9.0"
    private const val chuckVersion = "3.5.2"

    private const val workVersion = "2.8.1"

    private const val mockitoVersion = "2.7.22"
    private const val truthVersion = "0.34"
    private const val junitVersion = "4.12"
    private const val espressoVersion = "3.1.0"
    private const val runnerVersion = "1.1.0"

    private const val mediaVersion = "1.0.1"

    private const val cameraxVersion = "1.2.0-beta01"
    private const val guavaVersion = "31.1-android"

    private const val facebookVersion = "16.0.0"

    private const val barcodeVersion = "4.3.0"

    private const val uploadServiceVersion = "4.7.0"

    private const val videoCompressorVersion = "1.2.3"

    private const val apivideoVersion = "1.3.2"

    private const val trustWeb3Version = "2.0.8"
    private const val trustWalletCoreVersion = "3.1.20"

    object plugin {
        const val application = "com.android.application"
        const val library = "com.android.library"
        const val kotlin_kapt = "kotlin-kapt"
        const val kotlin_parcelize = "kotlin-parcelize"
        const val kotlin_serialization = "kotlinx-serialization"
        const val kotlin_android = "kotlin-android"
        const val hilt = "dagger.hilt.android.plugin"
        const val lint = "org.jmailen.kotlinter"
        const val google_services = "com.google.gms.google-services"
        const val crashlytics = "com.google.firebase.crashlytics"
        const val spotify_ruler = "com.spotify.ruler"
        const val ben_manes_versions = "com.github.ben-manes.versions"
    }

    object classpath {
        const val google_services = "com.google.gms:google-services:$gmsVersion"
        const val android_gradle = "com.android.tools.build:gradle:$gradleVersion"
        const val kotlin_gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        const val kotlin_serialization = "org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion"
        const val crashlytics_gradle = "com.google.firebase:firebase-crashlytics-gradle:$crashlyticsGradleVersion"
        const val hilt_gradle = "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion"
        const val navigation_safeArgs_gradle = "androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion"
        const val kotlinter = "org.jmailen.gradle:kotlinter-gradle:$kotlinterVersion"
        const val ruler = "com.spotify.ruler:ruler-gradle-plugin:$rulerVersion"
        const val ben_manes_versions = "com.github.ben-manes:gradle-versions-plugin:$benManesVersionsVersion"
    }

    object constraints {
        /*val kotlin = arrayOf(
            "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion",
        )*/
    }

    object bundle {
        val kotlin = arrayOf("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
        val coroutines = arrayOf(
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion",
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion",
            "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion"
        )
        val kotlinSerialization = arrayOf(
            "org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinSerializationVersion",
            "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion"
        )

        val desugar = arrayOf("com.android.tools:desugar_jdk_libs:$desugarVersion")

        val hilt = arrayOf(
            "com.google.dagger:hilt-android:$hiltVersion",
            "androidx.hilt:hilt-navigation-compose:$hiltJetpackVersion",
        )
        const val hiltKapt = "com.google.dagger:hilt-android-compiler:$hiltVersion"
        const val hiltKaptViewModel = "androidx.hilt:hilt-compiler:$hiltJetpackVersion"

        const val playServicesAuth = "com.google.android.gms:play-services-auth:$playServicesAuthVersion"
        const val recaptcha = "com.google.android.recaptcha:recaptcha:$recaptchaVersion"

        val firebaseBom = "com.google.firebase:firebase-bom:$firebaseBomVersion"
        val firebase = arrayOf(
            "com.google.firebase:firebase-auth-ktx",
            "com.google.firebase:firebase-crashlytics-ktx",
            "com.google.firebase:firebase-analytics-ktx",
            "com.google.firebase:firebase-messaging-ktx",
            "com.google.firebase:firebase-config-ktx",
            "com.google.firebase:firebase-dynamic-links-ktx",
        )

        val composeCompiler = arrayOf("androidx.compose.compiler:compiler:$composeCompilerVersion")
        val composeRuntime = arrayOf("androidx.compose.runtime:runtime:$composeVersion")
        val composeTheme = arrayOf(
            "androidx.compose.ui:ui:$composeVersion",
            "androidx.compose.ui:ui-tooling:$composeVersion",
            "androidx.compose.ui:ui-tooling-preview:$composeVersion",
            "androidx.compose.material:material:$composeMaterialVersion",
            "androidx.compose.material:material-icons-extended:$composeMaterialVersion",
            "androidx.compose.material3:material3:$composeMaterial3Version",
            "androidx.compose.material3:material3-window-size-class:$composeMaterial3Version",
            "com.google.android.material:material:$materialVersion",
        )
        val composeWidgetTheme = arrayOf("androidx.glance:glance-appwidget:$composeGlanceVersion")
        val composeKit = arrayOf(
            "androidx.activity:activity-compose:$activityVersion",
            "androidx.compose.foundation:foundation:$composeFoundationVersion",
            "androidx.compose.foundation:foundation-layout:$composeFoundationVersion",
            "androidx.compose.animation:animation:$composeVersion",
            "com.google.accompanist:accompanist-pager-indicators:$accompanistVersion",
            "com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion",
            "com.google.accompanist:accompanist-placeholder-material:$accompanistVersion",
            "com.google.accompanist:accompanist-permissions:$accompanistVersion",
            "io.coil-kt:coil-compose:$coilVersion",
            "io.coil-kt:coil-svg:$coilVersion",
            "com.airbnb.android:lottie-compose:$lottieVersion",
        )

        val splashscreen = arrayOf("androidx.core:core-splashscreen:$splashVersion")
        val paging = arrayOf("androidx.paging:paging-runtime:$pagingVersion")

        val lifecycle = arrayOf(
            "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion",
            "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion",
            "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion",
            "androidx.lifecycle:lifecycle-process:$lifecycleVersion",
        )
        const val lifecycleKapt = "androidx.lifecycle:lifecycle-compiler:$lifecycleVersion"

        val navigation = arrayOf(
            "androidx.navigation:navigation-compose:$navigationVersion",
        )

        val appUpdater = arrayOf("com.google.android.play:core-ktx:$appUpdaterVersion")
        val billing = arrayOf(
            "com.android.billingclient:billing-ktx:$billingVersion",
            "com.android.billingclient:billing:$billingVersion",
        )
        val browser = arrayOf("androidx.browser:browser:$browserVersion")
        val biometric = arrayOf("androidx.biometric:biometric-ktx:$biometricVersion")
        val window = arrayOf("androidx.window:window:$windowVersion")

        val datastore = arrayOf("androidx.datastore:datastore-preferences:$datastoreVersion")
        val cryptoPreference = arrayOf("androidx.security:security-crypto:$securityVersion")
        val preference = arrayOf("androidx.preference:preference-ktx:$preferenceVersion")

        val room = arrayOf(
            "androidx.room:room-runtime:$roomVersion",
            "androidx.room:room-ktx:$roomVersion",
        )
        const val roomKapt = "androidx.room:room-compiler:$roomVersion"

        val requests = arrayOf(
            "com.squareup.retrofit2:retrofit:$retrofitVersion",
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$jsonConverterVersion",
            "com.squareup.okhttp3:okhttp:$okHttpVersion",
            "com.squareup.okhttp3:logging-interceptor:$okHttpVersion",
            "com.github.chuckerteam.chucker:library:$chuckVersion",
        )

        val work = arrayOf(
            "androidx.work:work-runtime-ktx:$workVersion",
        )

        val media = arrayOf(
            "androidx.media3:media3-exoplayer:$mediaVersion",
            "androidx.media3:media3-ui:$mediaVersion",
            "androidx.media3:media3-exoplayer-hls:$mediaVersion",
        )

        val camerax = arrayOf(
            "androidx.camera:camera-lifecycle:$cameraxVersion",
            "androidx.camera:camera-video:$cameraxVersion",
            "androidx.camera:camera-view:$cameraxVersion",
            "androidx.camera:camera-extensions:$cameraxVersion",
            "com.google.guava:guava:$guavaVersion",
        )

        val crypto = arrayOf(
            "com.github.horizontalsystems:bitcoin-kit-android:d1a55ca",
            /*"com.github.horizontalsystems:ethereum-kit-android:7aadf9d",*/
            "com.github.horizontalsystems:blockchain-fee-rate-kit-android:501cf1e",
            /*"com.github.horizontalsystems:binance-chain-kit-android:2a89cad",*/
            "com.github.horizontalsystems:market-kit-android:97f5bc7",
            "com.github.horizontalsystems:solana-kit-android:59c448a",
            "com.github.horizontalsystems:hd-wallet-kit-android:f46885a",
            "com.github.horizontalsystems:wallet-connect-kotlin:b9a50b8",
            "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.6.4",
            "io.reactivex.rxjava2:rxandroid:2.1.1",
            "androidx.room:room-rxjava2:2.4.3",
            /*"com.walletconnect:android-core:1.9.1",
            "com.walletconnect:sign:2.7.1",*/
            "androidx.work:work-runtime-ktx:2.7.1",
            "cash.z.ecc.android:zcash-android-sdk:1.9.0-beta04",
            "com.squareup.retrofit2:converter-gson:2.9.0",
            "com.squareup.retrofit2:adapter-rxjava2:2.9.0",
            "com.squareup.retrofit2:converter-scalars:2.9.0",
        )
        val trustWallet = arrayOf(
            "com.github.trustwallet:trust-web3-provider:$trustWeb3Version",
            "com.trustwallet:wallet-core:$trustWalletCoreVersion"
        )

        val gson = arrayOf(
            "com.google.code.gson:gson:2.9.0",
        )

        const val barcode = "com.journeyapps:zxing-android-embedded:$barcodeVersion"

        val uploadService = arrayOf(
            "net.gotev:uploadservice:$uploadServiceVersion",
            "net.gotev:uploadservice-okhttp:$uploadServiceVersion",
        )

        val videoCompressor = arrayOf(
            "com.github.AbedElazizShe:LightCompressor:$videoCompressorVersion",
        )

        val apivideo = arrayOf(
            "video.api:android-api-client:$apivideoVersion",
        )

        val facebook = arrayOf(
            "com.facebook.android:facebook-android-sdk:$facebookVersion",
        )

        const val unitTestsRunner = "android.support.test.runner.AndroidJUnitRunner"
        val unitTests = arrayOf(
            "junit:junit:$junitVersion",
            "org.mockito:mockito-core:$mockitoVersion",
            "com.google.truth:truth:$truthVersion",
        )
        val uiTests = arrayOf(
            "androidx.test.espresso:espresso-core:$espressoVersion",
            "androidx.test.espresso:espresso-intents:$espressoVersion",
            "androidx.test:runner:$runnerVersion",
            "org.mockito:mockito-core:$mockitoVersion",
            "com.google.truth:truth:$truthVersion",
        )
    }
}