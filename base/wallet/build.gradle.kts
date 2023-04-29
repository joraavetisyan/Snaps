plugins {
    id(Libs.plugin.library)
}

common()
compose()
dagger()
lifecycle()

dependencies {
    implementation(projects.ethereumkit)
    implementation(projects.erc20kit)

    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)
    implementation(projects.coreCrypto)

    implementation(projects.baseSources)

    implementation(*Libs.bundle.crypto)
    implementation(*Libs.bundle.trustWallet)
}