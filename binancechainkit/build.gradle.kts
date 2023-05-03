import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    id(Libs.plugin.library)
    id("com.google.protobuf") version "0.9.3"
}

common()

android {
    namespace = "io.horizontalsystems.binancechainkit"
}

sourceSets {
    create("main") {
        java.srcDir("${protobuf.generatedFilesBaseDir}/main/protobuf")
    }
}

dependencies {
    implementation("org.bouncycastle:bcpkix-jdk15on:1.65")
    implementation("com.github.horizontalsystems:hd-wallet-kit-android:f46885a")
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.squareup.retrofit2:retrofit:2.8.1")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.8.1")
    implementation("com.squareup.retrofit2:converter-gson:2.8.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-rxjava2:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")
    implementation("androidx.annotation:annotation:1.1.0")
    kapt("org.projectlombok:lombok:1.18.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9")
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.9")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.9.9")
    implementation("com.google.protobuf:protobuf-javalite:3.14.0")
    implementation("com.google.guava:guava:28.1-android")
}

protobuf {
    val archSuffix = if (Os.isFamily(Os.FAMILY_MAC)) ":osx-x86_64" else ""

    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0$archSuffix"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}