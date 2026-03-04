import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.davi.dev.scannermlkit"
    compileSdk = 36
    ndkVersion = "28.0.12433566"

    defaultConfig {
        applicationId = "com.davi.dev.scannermlkit"
        minSdk = 34
        targetSdk = 36
        versionCode = 7
        versionName = "1.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {
            // For ndk-build, instead use the ndkBuild block.
            cmake {
                // Passes optional arguments to CMake.
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
            }
        }
    }

    // Opcional: Garante que as libs não sejam comprimidas no APK
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                ?: keystoreProperties["keyAlias"] as? String

            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
                ?: keystoreProperties["keyPassword"] as? String

            storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                ?: keystoreProperties["storePassword"] as? String

            // O arquivo JKS no GitHub Actions será decodificado na raiz do app/
            val storeFilePath = System.getenv("RELEASE_STORE_FILE")
                ?: keystoreProperties["storeFile"] as? String

            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
            }

        }

        getByName("debug") {
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                ?: keystoreProperties["keyAlias"] as? String

            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
                ?: keystoreProperties["keyPassword"] as? String

            storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                ?: keystoreProperties["storePassword"] as? String

            // O arquivo JKS no GitHub Actions será decodificado na raiz do app/
            val storeFilePath = System.getenv("RELEASE_STORE_FILE")
                ?: keystoreProperties["storeFile"] as? String

            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
            }

        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.compose.material)

    implementation(libs.androidx.compose.material.icons.extended)

    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.4.0")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.5.0-alpha14")

    implementation(libs.accompanist.permissions)

    implementation(libs.play.services.mlkit.document.scanner)

    implementation("com.itextpdf.android:kernel-android:9.5.0")
    implementation("com.itextpdf.android:io-android:9.5.0")
    implementation("com.itextpdf.android:layout-android:9.5.0")
    implementation("com.itextpdf.android:bouncy-castle-adapter-android:9.5.0")
    implementation("com.itextpdf.android:bouncy-castle-connector-android:9.5.0")

    implementation(libs.barcode.scanning)
    // CameraX Dependencies
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation("com.airbnb.android:lottie-compose:6.7.1")

    implementation(platform("io.github.jan-tennert.supabase:bom:3.4.1"))
    implementation("io.github.jan-tennert.supabase:auth-kt")

    implementation("io.ktor:ktor-client-android:3.4.0")

    implementation("androidx.credentials:credentials:1.6.0-rc02")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0-rc02")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.0")

    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    implementation("androidx.datastore:datastore-preferences:1.2.0")

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.fragment.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.compose.ui.text.google.fonts)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}