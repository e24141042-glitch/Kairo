// build.gradle.kts
plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
