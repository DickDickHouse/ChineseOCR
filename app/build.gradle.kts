plugins {
    kotlin("jvm") version "1.5.30"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.google.cloud:tesseract-ocr:4.0.0")
}