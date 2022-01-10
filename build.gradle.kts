// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
  val agpVersion = "7.1.0-rc01"
  val kotlinVersion = "1.6.10"
  id("com.android.application") version agpVersion apply false
  id("com.android.library") version agpVersion apply false
  kotlin("jvm") version kotlinVersion apply false
}

buildscript {
  dependencies {
    classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.2.0")
  }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}