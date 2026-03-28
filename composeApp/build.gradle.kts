import com.android.build.api.dsl.ApplicationBaseFlavor
import com.android.build.api.variant.VariantOutputConfiguration
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

android {
    namespace = "io.github.kkoshin.muse.app"
    compileSdk = libs.versions.android.compileSdk
    .get()
    .toInt()

    sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/main/res")

    defaultConfig {
        applicationId = "io.github.kkoshin.muse"
        minSdk = libs.versions.android.minSdk
            .get()
            .toInt()
        targetSdk = libs.versions.android.targetSdk
            .get()
            .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.clear()
            //noinspection ChromeOsAbiSupport
            abiFilters += "arm64-v8a"
        }
    }

    buildTypes {
        release {
            // 这里不配置签名，对应操作在外部进行
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":muse"))
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.ui)

    implementation(libs.navigation.compose)
    implementation(libs.xcrash)
    implementation(sharedLibs.logcat)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.sugar)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(sharedLibs.bundles.jetpack)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.browser)

    debugImplementation(libs.devtools)
}

private fun ApplicationBaseFlavor.setUpStableVersion(
    major: Int = 0,
    minor: Int = 1,
    patch: Int = 0,
    code: Int,
) {
    versionName = "$major.$minor.$patch"
    versionCode = code
}

androidComponents {
    finalizeDsl { extension ->
        extension.defaultConfig.setUpStableVersion(
            major = 0, // breaking change
            minor = 2, // feature
            patch = 0, // bugfix
            code = 6,
        )
        extension.buildTypes.getByName("debug").apply {
            applicationIdSuffix = ".debug"
            ndk.abiFilters += "x86_64"
        }
    }
    // only rename the apk file for single-apk build
    onVariants(selector().withBuildType("release")) { variant ->
        variant.outputs.filterIsInstance<com.android.build.api.variant.impl.VariantOutputImpl>()
            .filter { it.outputType == VariantOutputConfiguration.OutputType.SINGLE }
            .forEach {
                it.outputFileName = "Muse-${it.versionName.get()}.apk"
            }
    }
}
