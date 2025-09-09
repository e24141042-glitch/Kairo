// build.gradle.kts
plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
