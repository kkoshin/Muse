import com.android.build.api.dsl.ApplicationBaseFlavor
import com.android.build.api.variant.VariantOutputConfiguration
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.about)
}

aboutLibraries {
    // Remove the "generated" timestamp to allow for reproducible builds
    excludeFields = arrayOf("generated")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.navigation)
            implementation(sharedLibs.logcat)
            implementation(libs.sugar)
            implementation(dependencies.create(sharedLibs.koin.asProvider().get()).toString()) {
                exclude(group = "androidx.appcompat")
            }
            implementation(sharedLibs.bundles.jetpack)
            implementation(libs.documentfile)
            implementation(dependencies.create(libs.lame.get()).toString()) {
                exclude(group = "com.android.support")
            }
            implementation(libs.browser)
            implementation(libs.sql.android)
            implementation(libs.xcrash)
            implementation(libs.bundles.media3)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.json)
            implementation(project(":elevenlabs"))
            implementation(libs.accompanist.navigation.material)
            implementation(libs.bundles.about)
            implementation(libs.bytesize)
        }
    }
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
        ndk {
            abiFilters.clear()
            //noinspection ChromeOsAbiSupport
            abiFilters += "arm64-v8a"
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    dependencies {
        debugImplementation(compose.preview)
        debugImplementation(compose.uiTooling)
        implementation(platform(sharedLibs.koin.bom))
        debugImplementation(libs.devtools)
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("io.github.kkoshin.muse.database")
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

androidComponents {
    finalizeDsl { extension ->
        extension.defaultConfig.setUpStableVersion(
            major = 0, // breaking change
            minor = 1, // feature
            patch = 3, // bugfix
            code = 4,
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

