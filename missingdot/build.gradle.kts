import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests

buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.library") version "4.2.2"
    id("org.jetbrains.kotlin.multiplatform") version "1.5.21"
    id("org.jetbrains.dokka") version "1.5.0"
    id("maven-publish")
    id("signing")
}

kotlin {
    android {
        publishLibraryVariantsGroupedByFlavor = true
        publishLibraryVariants("debug", "release")
    }
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(BOTH) {
        nodejs {
            testTask {
                enabled = false //   :missingdot:jsLegacyBrowserTest: java.lang.IllegalStateException: command '/home/runner/.gradle/nodejs/node-v14.15.4-linux-x64/bin/node' exited with errors (exit code: 1):missingdot:jsLegacyNodeTest: java.lang.IllegalStateException: command '/home/runner/.gradle/nodejs/node-v14.15.4-linux-x64/bin/node' exited with errors (exit code: 1)
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
            useCommonJs()
        }
        browser {
            testTask {
                enabled = false
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")

    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native") {
            binaries.staticLib()
            binaries.sharedLib()
        }
        hostOs == "Linux" -> linuxX64("native") {
            binaries.staticLib()
            binaries.sharedLib()
        }
        isMingwX64 -> mingwX64("native") {
            binaries.staticLib()
            binaries.sharedLib()
        }
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].assets.srcDir("src/commonMain/resources") // kind of hack...
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
    }
    buildTypes {
        val debug by getting {
            minifyEnabled(false)
        }
        val release by getting {
            minifyEnabled(false)
        }
    }
}

afterEvaluate {

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications.withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("missing-dot")
                description.set("a migration helper library from .NET to Kotlin MPP")
                url.set("https://github.com/atsushieno/missing-dot")
                scm {
                    url.set("https://github.com/atsushieno/missing-dot")
                }
                licenses {
                    license {
                        name.set("the MIT License")
                        url.set("https://github.com/atsushieno/missing-dot/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("atsushieno")
                        name.set("Atsushi Eno")
                        email.set("atsushieno@gmail.com")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "OSSRH"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }
    }

    // keep it as is. It is replaced by CI release builds
    signing {}
}
