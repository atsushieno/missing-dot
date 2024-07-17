import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`

plugins {
    `maven-publish`
    signing
}

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {
        // disabled https://github.com/vanniktech/gradle-maven-publish-plugin/issues/754
        // and configured at library build.gradle.kts using `JavadocJar.Dokka("dokkaHtml")`.
        /*
        // Stub javadoc.jar artifact
        artifact(tasks.register("${name}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(this@withType.name)
        })*/

        // Provide artifacts information required by Maven Central
        pom {
            name.set("missing-dot")
            description.set("a migration helper library from .NET to Kotlin Multiplatform")
            url.set("https://github.com/atsushieno/missing-dot")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("atsushieno")
                    name.set("Atsushi Eno")
                    organization.set("atsushieno")
                }
            }
            scm {
                url.set("https://github.com/atsushieno/missing-dot.git")
            }
        }
    }
}

signing {
    if (project.hasProperty("mavenCentralUsername") ||
        System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername") != null) {
        useGpgCmd()
        // It is not perfect (fails at some dependency assertions), better handled as
        // `signAllPublications()` (as in vanniktech maven publish plugin) at build.gradle.kts.
        //sign(publishing.publications)
    }
}
