enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            // 如何获取id: https://stackoverflow.com/questions/74221701/how-to-find-gradle-plugin-id-for-given-gradle-library
            if (requested.id.id.startsWith("com.tencent.matrix-plugin")) {
                useModule("com.tencent.matrix:matrix-gradle-plugin:2.1.0")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("sharedLibs") {
            from("io.github.foodiestudio:libs-versions:2023.08.01")
            // workaround: 最新 compose-ui 与 lifecycle 2.8.0 冲突
            version("lifecycle", "2.8.2")
        }
    }
}

include(":composeApp")
include(":elevenlabs")