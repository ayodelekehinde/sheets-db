val ktor_version: String by project
plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("io.kotest.multiplatform") version "5.0.2"
    id("maven-publish")
    id("signing")
}

group = "io.github.cherrio-llc"
version = "0.0.1-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting{
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-cio:$ktor_version")
                implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }

        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-assertions-core:5.5.1")
                implementation("io.kotest:kotest-framework-engine:5.5.1")

            }
        }
        val jvmMain by getting
        val jvmTest by getting{
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:5.5.1")
            }
        }
    }
}
publishing {
    repositories {
        maven {
            val isSnapshot = version.toString().endsWith("SNAPSHOT")
            url = uri(
                if (!isSnapshot) "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
                else "https://s01.oss.sonatype.org/content/repositories/snapshots"
            )

            credentials {
                val mavenUsername = System.getenv("MAVEN_USERNAME")
                val mavenPassword = System.getenv("MAVEN_PASSWORD")
                username = mavenUsername
                password = mavenPassword
            }
        }
    }

    // TODO: Use dokka to generate javadoc instead of empty ones
    val javadocJar = tasks.register("javadocJar", Jar::class.java) {
        archiveClassifier.set("javadoc")
    }

    publications {
        withType<MavenPublication> {
            artifact(javadocJar)

            pom {
                name.set("SheetsDB")
                description.set("A lightweight Google Sheets API wrapper using kotlinx.coroutines, kotlinx.serialisation and Ktor")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                url.set("https://cherrio-llc.github.io/sheets-db/")
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/cherrio-llc/sheets-db/issues")
                }
                scm {
                    connection.set("https://github.com/cherrio-llc/sheets-db.git")
                    url.set("https://github.com/cherrio-llc/sheets-db")
                }
                developers {
                    developer {
                        name.set("Ayodele Kehinde")
                        email.set("cherrio.llc@gmail.com")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}