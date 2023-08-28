import org.flywaydb.gradle.task.FlywayMigrateTask

plugins {
    idea
    java
    `maven-publish`
    jacoco
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.freefair.lombok") version "8.0.1"
    id("nu.studer.jooq") version "8.2"
    id("org.flywaydb.flyway") version "9.14.1"
}

group = "com.pauldaniv.promotion.yellowtaxi.totals"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val awsDomainOwner: String? = System.getenv("AWS_DOMAIN_OWNER_ID")
val codeArtifactRepository = "https://promotion-${awsDomainOwner}.d.codeartifact.us-east-2.amazonaws.com/maven/releases/"
val codeArtifactPassword: String? = System.getenv("CODEARTIFACT_AUTH_TOKEN")

repositories {
    mavenCentral()
    mavenLocal()
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
    implementation("com.pauldaniv.promotion.yellowtaxi:persistence:0.0.6-SNAPSHOT")
    implementation("com.pauldaniv.promotion.yellowtaxi:api:0.0.6-SNAPSHOT")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("redis.clients:jedis")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testng:testng:7.8.0")
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
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
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}


tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.withType<Test> {
    useTestNG()
}

tasks.withType<Test> {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.withType<JacocoReport> {
    afterEvaluate {
        classDirectories.setFrom(classDirectories.files.map {
            fileTree(it).matching {
                exclude(
                    "**/config",
                    "**/model",
                    "**/*Application*"
                )
            }
        })
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
//            rule {
//                limit {
//                    minimum = "0.9".toBigDecimal()
//                }
//            }

        rule {
//                element = "CLASS"
//                includes = listOf("com.pauldaniv.*")
            classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
        }
    }
}

tasks.withType<nu.studer.gradle.jooq.JooqGenerate> {
    dependsOn(tasks.flywayMigrate)
}

val dbHost = findParam("DB_HOST") ?: "localhost"
val dbPort = findParam("DB_PORT") ?: 5432
val dbUser = findParam("DB_USER") ?: "service"
val dbPass = findParam("DB_PASS") ?: "letmeeeen"
val dbName = findParam("DB_NAME") ?: "service"


flyway {
    url = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
    user = dbUser
    password = dbPass
    schemas = arrayOf("public")
    locations = arrayOf("classpath:migration/postgres")
}

tasks.test {
    dependsOn(tasks.flywayMigrate)
}

fun findParam(name: String): String? = project.findProperty(name) as String? ?: System.getenv(name)

val containers = listOf("db")

tasks.register("startServices") {
    doLast {
        containers.forEach {
            if (isDockerRunning(it)) {
                println("$it service is already running. Skipping...")
            } else {
                println("Bringing up containers...")
                startContainers()
            }
        }
    }
}

tasks.register("stopServices") {
    doLast {
        containers.forEach {
            if (isDockerRunning(it)) {
                stopService(it)
            } else {
                println("$it service is already stopped")
            }
        }
    }
}

tasks.clean {
    dependsOn(tasks.findByName("stopServices"))
}

tasks.flywayMigrate {
    dependsOn(tasks.findByName("startServices"))
}

fun isDockerRunning(containerName: String) = listOf("docker", "inspect", "-f", "'{{json .State.Running}}'", containerName)
    .exec().apply { println("ServiceState: $this") }.replace("'", "").toBoolean()

fun isServiceHealthy(containerName: String) =
    if (containerName == "yt-db")
        isPostgresHealthy()
    else
        isDockerRunning(containerName)

// todo: use postgres driver instead of console command
fun isPostgresHealthy() =
    listOf("psql", "-U", dbUser, "-h", dbHost, "-c", "select version()")
        .exec(envs = mapOf("PGPASSWORD" to dbPass)).apply {
            println("Response from postgres healthcheck: $this")
        }
        .contains("PostgreSQL 15.*compiled by".toRegex())

fun startContainers() {
    "docker compose -f ${rootProject.projectDir}/services.yaml up -d".exec()
    println("Waiting for postgres to be healthy...")
    waitTillHealthy("yt-db")
}

fun stopService(containerName: String) {
    "docker compose -f ${rootProject.projectDir}/services.yaml rm --stop --volumes $containerName --force"
        .exec()
        .apply { println(this) }
}

fun waitTillHealthy(service: String) {
    var count = 0
    val retries = 50
//    if (System.getenv("GITHUB_ACTIONS").toBoolean()) {
//        println("Detected GitHub Actions env. Skipping postgres checks...")
//        return
//    }
    while (!isServiceHealthy(service) && count < retries) {
        count++
        Thread.sleep(1000L)
        println(count)
        println("Retrying...")
    }
    if (count >= retries) {
        println("Unable to bring up $service service...")
    } else {
        println("Postgres container is up!")
    }
}

fun List<String>.exec(workingDir: File = file("./"), envs: Map<String, String> = mapOf()): String {
    val procBuilder = ProcessBuilder(*this.toTypedArray())
        .directory(workingDir)
        .redirectErrorStream(true)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)

    procBuilder.environment().putAll(envs)

    val proc = procBuilder.start()

    proc.waitFor(1, TimeUnit.MINUTES)
    return proc.inputStream.bufferedReader().readLines().joinToString("\n")
}

fun String.exec(envs: Map<String, String> = mapOf()): String {
    val parts = this.split("\\s".toRegex())
    return parts.toList().exec(envs = envs)
}


fun getParam(name: String, default: String? = ""): String? {
    return System.getenv(name) ?: default
}
