pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/snapshot") }
		gradlePluginPortal()
	}
	plugins {
		kotlin("jvm") version "1.9.24"
		id("org.sonarqube") version "5.0.0.4638"
	}
}
rootProject.name = "GIPHTTP_MO"
