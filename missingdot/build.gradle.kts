import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("module.publication")
    alias(libs.plugins.vanniktech.maven.publish)
    alias(libs.plugins.dokka)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    jvmToolchain(21)
    targets.withType<KotlinJvmTarget> {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            // https://jakewharton.com/kotlins-jdk-release-compatibility-flag/
            freeCompilerArgs.addAll("-Xjdk-release=11", "-jvm-target=11")
        }
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "missingdot"
        browser {}
        nodejs {}
    }
    jvm {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js {
        browser {}
        nodejs {}
    }
    androidTarget {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "dev.atsushieno.missingdot"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    configure(KotlinMultiplatform(javadocJar = JavadocJar.Dokka("dokkaHtml")))
    publishToMavenCentral(SonatypeHost.S01)
    if (project.hasProperty("mavenCentralUsername") ||
        System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername") != null)
        signAllPublications()
}
