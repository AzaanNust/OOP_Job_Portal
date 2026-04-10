// ============================================================
// build.gradle.kts
// Job Portal Resume Matching System
// Spring Boot 3.4.5 | JDK 21 | Gradle Kotlin DSL
// ============================================================

plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group   = "OOP.JobPortal"
version = "1.0.0"

// Tell Gradle we are using Java 21
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// ── Spring Boot Core ──────────────────────────────────────────
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// ── WebFlux (needed for WebClient to call Claude AI API) ──────
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// ── MySQL Database Driver ─────────────────────────────────────
	runtimeOnly("com.mysql:mysql-connector-j")

	// ── JWT (JSON Web Token) for stateless authentication ─────────
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	// ── PDF Generation (convert HTML resume to PDF) ───────────────
	implementation("com.itextpdf:itext-core:9.1.0")
	implementation("com.itextpdf:html2pdf:6.1.0")

	// ── Swagger / OpenAPI (interactive API documentation) ─────────
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

	// ── Jackson (JSON serialization / deserialization) ────────────
	implementation("com.fasterxml.jackson.core:jackson-databind")

	// ── Dev Tools (auto-restart on code change during development) ─
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// ── Testing ───────────────────────────────────────────────────
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}