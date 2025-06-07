import org.gradle.kotlin.dsl.implementation
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "2.0.21"
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.dias.installwifi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dias.installwifi"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        fun getSigningProperty(propertyName: String): String {
            val properties = Properties()
            val file = File(rootProject.projectDir, "local.properties")
            if (file.exists()) {
                FileInputStream(file).use { properties.load(it) }
            }

            return properties.getProperty(propertyName, System.getenv(propertyName) ?: "")
        }

        buildConfigField("String", "SUPABASE_URL", "\"${getSigningProperty("SUPABASE_URL")}\"")
        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            "\"${getSigningProperty("SUPABASE_ANON_KEY")}\""
        )

    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase Bom
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    // Supabase
    implementation(platform(libs.bom))
    implementation(libs.storage.kt)
    implementation(libs.ktor.client.android)

    // datastore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.activity.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Circle Image View
    implementation(libs.circleimageview)

    // Glide
    implementation(libs.glide)

    // Google
    implementation(libs.play.services.auth)

    // OkHttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
}