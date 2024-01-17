import org.jetbrains.kotlin.gradle.plugin.extraProperties

// Top-level build file where you can add configuration options common to all sub-projects/modules.


rootProject.extraProperties.set("hiltVersion", "2.48")


plugins {
    val kotlinVersion = "1.9.20"

    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("org.jetbrains.kotlin.kapt") version kotlinVersion apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version kotlinVersion apply false

}
