plugins {
  val kotlinVersion = "1.6.10"
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("de.mannodermaus.android-junit5")
  kotlin("plugin.serialization") version kotlinVersion
}

android {
  compileSdk = 32

  defaultConfig {
    applicationId = "com.example.acmeroute"
    minSdk = 24
    targetSdk = 32
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    viewBinding = true
  }

  buildTypes {
    named("release") {
      isMinifyEnabled = false
      setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {

  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.appcompat:appcompat:1.4.0")
  implementation("com.google.android.material:material:1.4.0")
  implementation("androidx.fragment:fragment-ktx:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.2")
  testImplementation("org.junit.jupiter:junit-jupiter")
  val lifecycleVersion = "2.4.0"
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

  // Kotlin serialization support
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

  // Groupie support for RecyclerView
  val groupieVersion = "2.9.0"
  implementation("com.github.lisawray.groupie:groupie:$groupieVersion")
  implementation("com.github.lisawray.groupie:groupie-viewbinding:$groupieVersion")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
  testImplementation("io.mockk:mockk:1.12.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}