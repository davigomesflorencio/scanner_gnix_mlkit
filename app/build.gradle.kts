import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
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
        versionCode = 9
        versionName = "1.7.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {
            // For ndk-build, instead use the ndkBuild block.
            cmake {
                // Passes optional arguments to CMake.
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
            }
        }

        val supabaseUrl: String = gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_URL") ?:  System.getenv("SUPABASE_URL")
        buildConfigField("String", "supabaseUrl", "$supabaseUrl")
        println(supabaseUrl)
        val supabaseKey: String = gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_KEY") ?:  System.getenv("SUPABASE_KEY")
        buildConfigField("String", "supabaseKey", "$supabaseKey")
        println(supabaseKey)
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
        buildConfig = true
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

    implementation(libs.itextpdf.kernel.android)
    implementation(libs.itextpdf.io.android)
    implementation(libs.itextpdf.layout.android)
    implementation(libs.itextpdf.bouncy.castle.adapter.android)
    implementation(libs.itextpdf.bouncy.castle.connector.android)

    implementation(libs.barcode.scanning)
    // CameraX Dependencies
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(libs.lottie.compose)

    implementation(platform(libs.bom))
    implementation(libs.auth.kt)

    implementation(libs.ktor.client.android)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.pdfbox.android)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.app.update.ktx)

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