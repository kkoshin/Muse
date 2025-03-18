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
        versionCode = 1
        versionName = "1.0"

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
    implementation(project(":feature"))
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)

    implementation(libs.navigation.compose)
    implementation(libs.xcrash)
    implementation(sharedLibs.logcat)
    implementation(platform(sharedLibs.koin.bom))
    implementation(dependencies.create(sharedLibs.koin.asProvider().get()).toString()) {
        exclude(group = "androidx.appcompat")
    }
    implementation(libs.sugar)
    implementation(compose.materialIconsExtended)
    implementation(libs.androidx.activity.compose)
    implementation(sharedLibs.bundles.jetpack)
    implementation(libs.accompanist.navigation.material)
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
            minor = 1, // feature
            patch = 2, // bugfix
            code = 3,
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