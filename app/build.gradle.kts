import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.onuray.midax"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.onuray.midax"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
        buildConfig = true
    }
    packaging {
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
        resources.excludes.add("META-INF/androidx/room/room-compiler-processing/LICENSE.txt")
    }

    flavorDimensions += "tier"
    productFlavors {
        create("dev") {
            dimension = "tier"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            val baseUrl = (project.findProperty("MIDAX_DEBUG_BASE_URL") as String?)
                ?: "https://finnhub.io/api/v1/"
            val apiKey = gradleLocalProperties(
                rootDir,
                providers = providers
            ).getProperty("MIDAX_DEBUG_API_KEY")

            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
            buildConfigField("String", "MIDAX_PRO_API_KEY", "\"$apiKey\"")

            manifestPlaceholders["hostName"] = "dev.midax.app"
        }

        create("pro") {
            dimension = "tier"

            val baseUrl = (project.findProperty("MIDAX_PRO_BASE_URL") as String?)
                ?: "https://finnhub.io/api/v1/"
            val apiKey = gradleLocalProperties(
                rootDir,
                providers = providers
            ).getProperty("MIDAX_PRO_API_KEY")

            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
            buildConfigField("String", "MIDAX_PRO_API_KEY", "\"$apiKey\"")

            manifestPlaceholders["hostName"] = "app.midax.com"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.compose.navigation)
    implementation(libs.hilt)
    implementation(libs.hilt.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.compiler)
    implementation(libs.room.paging)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.coil)
    implementation(libs.coroutine)
    implementation(libs.work.runtime)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    ksp(libs.room.compiler)
    ksp(libs.moshi.kotlin.codegen)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
    resolutionStrategy {
        force(libs.kotlin.stdlib)
    }
}