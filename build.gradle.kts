import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("io.gitlab.arturbosch.detekt") version "1.7.4"
	kotlin("jvm") version "1.3.61"
	kotlin("plugin.spring") version "1.3.61"
	kotlin("plugin.jpa") version "1.3.61"
}

group = "br.com.devcave.api"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	jcenter()
}

val springCloudVersion = "Hoxton.SR3"
val swaggerVersion = "2.9.2"
val mockkVersion = "1.9.3"
val fakerVersion = "0.16"
val restAssuredVersion = "4.1.2"
val testContainersVersion = "1.13.0"
val mockkSpringVersion = "2.0.1"
val detektVersion = "1.7.4"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	implementation("io.springfox:springfox-swagger2:$swaggerVersion")
	implementation("io.springfox:springfox-swagger-ui:$swaggerVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("io.mockk:mockk:$mockkVersion")
	testImplementation("com.github.javafaker:javafaker:$fakerVersion")
	testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
	testImplementation("io.rest-assured:json-path:$restAssuredVersion")
	testImplementation("io.rest-assured:xml-path:$restAssuredVersion")

	testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
	testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
	testImplementation("com.ninja-squad:springmockk:$mockkSpringVersion")

	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

task<Test>("unitTests") {
	useJUnitPlatform() {
		excludeTags("integration")
		includeTags("unit")
	}
}

task<Test>("integrationTests") {
	useJUnitPlatform() {
		includeTags("integration")
		excludeTags("unit")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

detekt {
	input = files("src/main/kotlin", "src/test/kotlin")
	config = files("custom-detekt.yml")
}
