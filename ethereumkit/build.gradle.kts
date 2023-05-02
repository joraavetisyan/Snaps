plugins {
    id(Libs.plugin.library)
}

common()

android {
    namespace = "io.horizontalsystems.ethereumkit"
}

dependencies {
    implementation("org.bouncycastle:bcpkix-jdk15on:1.65")
    implementation("com.github.horizontalsystems:hd-wallet-kit-android:f46885a")
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.tinder.scarlet:scarlet:0.1.12")
    implementation("com.tinder.scarlet:websocket-okhttp:0.1.12")
    implementation("com.tinder.scarlet:stream-adapter-rxjava2:0.1.12")
    implementation("com.tinder.scarlet:message-adapter-gson:0.1.12")
    implementation("com.tinder.scarlet:lifecycle-android:0.1.12")
    implementation("androidx.room:room-runtime:2.4.3")
    implementation("androidx.room:room-rxjava2:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")
    implementation("org.web3j:crypto:4.9.4")
    implementation("androidx.annotation:annotation:1.4.0")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}