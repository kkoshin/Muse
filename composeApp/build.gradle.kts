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
        create("play") {
            // Google Play 渠道：不打包 xcrash（其预编译 .so 非 16 KB 对齐，会被 Play 拒收）。
            // matchingFallbacks 让 :muse 等依赖回退到各自的 release 变体。
            matchingFallbacks += "release"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    // play 变体的 manifest 覆盖：移除只服务于 xcrash 的 CrashLogActivity。
    // 显式指定路径，避免依赖 KMP v2 布局的默认值。
    sourceSets["play"].manifest.srcFile("src/androidPlay/AndroidManifest.xml")
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

    implementation(libs.navigation3.ui)
    // xcrash 只进 F-Droid / GitHub Release（release）与 debug 变体；play 变体不带
    releaseImplementation(libs.xcrash)
    debugImplementation(libs.xcrash)
    implementation(libs.logcat)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.sugar)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.jetpack)
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
            patch = 1, // bugfix
            code = 8,
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
