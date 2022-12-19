plugins {
	id("org.springframework.boot") version "2.5.5"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	id("com.avast.gradle.docker-compose") version "0.14.9"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

group = "com.exacta"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

extra["springCloudVersion"] = "2021.0.4"
val javaMoneyVersion = "1.1"
val jacksonJavaMoneyVersion = "1.3.0"
val jadiraVersion = "7.0.0.CR1"
val mockkVersion = "1.13.2"
val kotestVersion = "5.5.1"
val hibernateTypesVersion = "2.20.0"
val monetaVersion = "1.4.2"
val valiktorVersion = "0.12.0"

dependencies {
	implementation("org.springdoc:springdoc-openapi-webflux-core:1.6.9")
	implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.9")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.cloud:spring-cloud-stream")
	implementation("javax.money:money-api:$javaMoneyVersion")
	implementation("org.zalando:jackson-datatype-money:$jacksonJavaMoneyVersion")
	implementation("org.jadira.usertype:usertype.core:$jadiraVersion")
	implementation("org.javamoney:moneta:$monetaVersion")
	implementation("com.vladmihalcea:hibernate-types-52:$hibernateTypesVersion")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	runtimeOnly("org.flywaydb:flyway-core")
	runtimeOnly("org.postgresql:postgresql")

	implementation("org.valiktor:valiktor-javamoney:$valiktorVersion")
	implementation("org.valiktor:valiktor-core:$valiktorVersion")
	implementation("org.valiktor:valiktor-javatime:$valiktorVersion")
	implementation("org.valiktor:valiktor-spring-boot-starter:$valiktorVersion")

	testImplementation("org.valiktor:valiktor-test:$valiktorVersion")
	testImplementation("io.mockk:mockk:$mockkVersion")
	testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("io.kotest:kotest-assertions-core:5.1.0")
	testImplementation("io.kotest:kotest-runner-junit5:5.1.0")
	testImplementation("io.kotest:kotest-property:5.1.0")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

configurations.all {
	exclude(group = "org.mockito")
	exclude(group = "org.junit.jupiter")
	exclude(group = "org.junit.vintage")
}

tasks {
	compileKotlin {
		kotlinOptions {
			jvmTarget = "11"
			freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
		}
	}

	compileTestKotlin {
		kotlinOptions {
			jvmTarget = "11"
			freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
		}
	}

	test {
		useJUnitPlatform()
		systemProperty("spring.profiles.active", "local")
	}

	bootJar {
		archiveFileName.set("app.jar")
	}

}