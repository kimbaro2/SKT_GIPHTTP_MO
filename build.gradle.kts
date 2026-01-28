plugins {
	java
	id("org.springframework.boot") version "2.7.15"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"  // Kotlin 버전은 1.7~1.8 권장
	id("org.sonarqube") version "5.0.0.4638"
}

group = "com.infra.mo"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("commons-dbcp:commons-dbcp:1.4")
	implementation("commons-io:commons-io:2.11.0")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	implementation("com.altibase:altibase-jdbc:7.1.0.10.2")
	implementation("com.zaxxer:HikariCP")

	implementation(kotlin("stdlib-jdk8"))
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("net.java.dev.jna:jna:5.17.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// 빌드 후 jar 파일을 프로젝트 루트로 복사
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	finalizedBy("copyJarToRoot")
}

tasks.register<Copy>("copyJarToRoot") {
	from("build/libs/${project.name}-${version}.jar")
	into(".")
	rename { "GIPHTTP_MO-${version}.jar" }
	dependsOn("bootJar")
	mustRunAfter("jar", "inspectClassesForKotlinIC")
	doNotTrackState("Copying to project root directory with .gradle folder")
}

// SonarQube 설정 - Java와 Kotlin 모두 감지
sonar {
	properties {
		property("sonar.projectKey", "GIPHTTP_MO")
		property("sonar.projectName", "GIPHTTP_MO")
		property("sonar.projectVersion", version.toString())
		property("sonar.token", "sqp_ef042497cb41e1c58de846c7a53c6c490b35a880")

		// 소스 코드 경로
		property("sonar.sources", "src/main/java")
		property("sonar.tests", "src/test/java")

		// Java 설정
		property("sonar.java.source", "17")
		property("sonar.java.target", "17")
		property("sonar.java.binaries", "build/classes/java/main,build/classes/kotlin/main")
		property("sonar.java.test.binaries", "build/classes/java/test,build/classes/kotlin/test")

		// Kotlin 설정
		property("sonar.kotlin.detected", "true")

		// 파일 확장자 명시
		property("sonar.java.file.suffixes", ".java")
		property("sonar.kotlin.file.suffixes", ".kt")

		// 인코딩
		property("sonar.sourceEncoding", "UTF-8")

		// 제외할 파일/디렉토리
		property("sonar.exclusions",
			"**/bin/**,**/build/**,**/*.class,**/generated-sources/**,**/*.so,**/*.dll,**/*.pdf")

		// 테스트 제외
		property("sonar.test.exclusions", "**/test/**")

		// 소나큐브 운영서버
		property("sonar.host.url", "http://121.124.125.247:9000/")
	}
}