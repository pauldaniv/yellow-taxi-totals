plugins {
    idea
    java
    `maven-publish`
    id("org.springframework.boot") version "3.1.0-M2"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.freefair.lombok") version "8.0.1"
    id("nu.studer.jooq") version "8.2"
}

group = "com.pauldaniv.promotion.yellowtaxi.totals"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val awsDomainOwner: String = System.getenv("AWS_DOMAIN_OWNER_ID")
val codeArtifactRepository = "https://promotion-${awsDomainOwner}.d.codeartifact.us-east-2.amazonaws.com/maven/releases/"
val codeArtifactPassword: String? = System.getenv("CODEARTIFACT_AUTH_TOKEN")

repositories {
    maven {
        name = "CodeArtifact"
        url = uri(codeArtifactRepository)
        credentials {
            username = "aws"
            password = codeArtifactPassword
        }
    }
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://repo.spring.io/milestone") }

}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.pauldaniv.promotion.yellowtaxi:persistence:0.0.5-SNAPSHOT")
    implementation("com.pauldaniv.promotion.yellowtaxi:api:0.0.5-SNAPSHOT")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

//val sourcesJar by tasks.creating(Jar::class) {
//    archiveClassifier.set("sources")
//    from(sourceSets["main"].allSource)
//}

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
            from(components["java"])
//            artifact(sourcesJar)
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

fun getParam(name: String, default: String? = ""): String? {
    return System.getenv(name) ?: default
}

