@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://artifactory.img.ly/artifactory/imgly")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://artifactory.img.ly/artifactory/imgly")
        maven(url = "https://maven.pkg.github.com/trustwallet/wallet-core") {
            credentials {
                val properties = java.util.Properties().apply {
                    load(java.io.FileInputStream(File(rootDir, "local.properties")))
                }
                username = properties.getProperty("gpr.user")
                password = properties.getProperty("gpr.key")
            }
        }
    }
}

val rootProjectPathLength = rootDir.absolutePath.length
val excludedProjects = listOf(File(rootDir, "buildSrc"))

rootDir.findAllPotentialModuleDirs()
    .filter { it.list()!!.any { child -> child.startsWith("build.gradle") } }
    .filterNot { it in excludedProjects }
    .forEach { moduleDir ->

        val moduleName = moduleDir.absolutePath
            .substring(rootProjectPathLength)
            .replace(File.separator, "-")
            .replaceFirst('-', ':')

        include(moduleName)
        project(moduleName).projectDir = moduleDir
    }

fun File.findAllPotentialModuleDirs(): Sequence<File> = listFiles()!!.asSequence()
    .filter { it.isDirectory }
    .filterNot { it.isHidden }
    .filterNot { it.name.startsWith('.') }
    .filterNot { it.name == "build" }
    .filterNot { it.name == "src" }
    .flatMap { sequenceOf(it) + it.findAllPotentialModuleDirs() }