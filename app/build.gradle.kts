plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("io.github.takahirom.roborazzi") version "1.8.0-alpha-5"
}

android {
    namespace = "com.cs407.project"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cs407.project"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.testLogging {
                    events("passed", "failed", "standardOut", "standardError")
                    showExceptions = true
                    showCauses = true
                    showStackTraces = true
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                    showStandardStreams = true
                }
                it.outputs.upToDateWhen { false }
            }
        }
    }
}

dependencies {
    // Room dependencies
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // Add this line

    // Kotlin coroutines dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")  // Coroutine core library
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")  // Android-specific coroutine support

    // Other dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation("androidx.room:room-ktx:2.5.0")


    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.androidx.espresso.core)

    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    testImplementation("androidx.test.espresso:espresso-intents:3.6.1")
    testImplementation("androidx.test:rules:1.6.1")
    testImplementation("androidx.test:runner:1.6.2")

    testImplementation("io.github.takahirom.roborazzi:roborazzi:1.8.0-alpha-5")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:1.8.0-alpha-5")

    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-inline:3.11.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
