pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/snapshot") }
		gradlePluginPortal()
	}
	plugins {
		kotlin("jvm") version "1.9.24"
	}
}
rootProject.name = "SKT_GIPHTTP_MO"
