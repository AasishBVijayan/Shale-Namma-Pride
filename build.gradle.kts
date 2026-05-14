// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version by extra("1.9.22")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

        // ADD THIS LINE for Firebase/Google Services support
        classpath("com.google.gms:google-services:4.4.0")
    }
}

// Fixed Clean Task: ensure it uses layout.buildDirectory for newer Gradle versions
tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
