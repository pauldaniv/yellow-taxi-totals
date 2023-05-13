import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

plugins {
    java
    `maven-publish`
    id("org.springframework.boot") version "3.1.0-M2"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.freefair.lombok") version "8.0.1"
    id("nu.studer.jooq") version "8.2"
}

group = "com.pauldaniv.promotion.yellowtaxi"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val awsDomainOwner: String = System.getenv("AWS_DOMAIN_OWNER_ID")
val codeArtifactRepository = "https://promotion-${awsDomainOwner}.d.codeartifact.us-east-2.amazonaws.com/maven/releases/"
val codeArtifactPassword: String? = System.getenv("CODEARTIFACT_AUTH_TOKEN")

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven {
        name = "CodeArtifact"
        url = uri(codeArtifactRepository)
        credentials {
            username = "aws"
            password = codeArtifactPassword
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    runtimeOnly("org.postgresql:postgresql")
    jooqGenerator("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

publishing {
    repositories {
        maven {
            name = "CodeArtifactPackages"
            url = uri(codeArtifactRepository)
            credentials {
                username = "aws"
                password = codeArtifactPassword
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}

jooq {
    version.set("3.18.2")  // default (can be omitted)
//	edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)

    configurations {
        create("main") {  // name of the jOOQ configuration
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)

            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/service"
                    user = "service"
                    password = "letmeeeen"
//                    properties.add(Property().apply {
//                        key = "ssl"
//                        value = "true"
//                    })
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        forcedTypes.addAll(listOf(
                                ForcedType().apply {
                                    name = "varchar"
                                    includeExpression = ".*"
                                    includeTypes = "JSONB?"
                                },
                                ForcedType().apply {
                                    name = "varchar"
                                    includeExpression = ".*"
                                    includeTypes = "INET"
                                }
                        ))
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.pauldaniv.promotion.yellowtaxi.totals.jooq"
                        directory = "${buildDir}/generated/jooq/main"  // default (can be omitted)
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}


tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
