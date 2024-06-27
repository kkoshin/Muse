import com.android.build.api.dsl.ApplicationBaseFlavor
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.navigation)
            implementation(sharedLibs.logcat)
            implementation(sharedLibs.sugar)
            implementation(dependencies.create(sharedLibs.koin.asProvider().get()).toString()) {
                exclude(group = "androidx.appcompat")
            }
            implementation(sharedLibs.bundles.jetpack)
            implementation(libs.documentfile)
            implementation(dependencies.create(libs.lame.get()).toString()) {
                exclude(group = "com.android.support")
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
//            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.json)
            implementation(project(":elevenlabs"))
        }
    }
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

android {
    namespace = "io.github.kkoshin.muse"
    compileSdk = libs.versions.android.compileSdk
        .get()
        .toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "io.github.kkoshin.muse"
        minSdk = libs.versions.android.minSdk
            .get()
            .toInt()
        targetSdk = libs.versions.android.targetSdk
            .get()
            .toInt()
        setUpStableVersion(
            major = 0, // breaking change
            minor = 1, // feature
            patch = 0, // bugfix
            code = 1,
        )
        versionNameSuffix = "-alpha2"

        ndk {
            abiFilters.clear()
            //noinspection ChromeOsAbiSupport
            abiFilters += "arm64-v8a"
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "Muse-${defaultConfig.versionName + (defaultConfig.versionNameSuffix ?: "")}-${
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                    ).format(Date())
                }.apk"
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            ndk {
                abiFilters += "x86_64"
            }
        }
        release {
            // 这里不配置签名，对应操作在外部进行
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.preview)
        debugImplementation(compose.uiTooling)
        // video export 仅作为 debug 功能
        debugImplementation(libs.bundles.media3)
        implementation(platform(sharedLibs.koin.bom))
    }
}
