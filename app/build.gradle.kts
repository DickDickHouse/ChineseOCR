plugins {
    id("com.android.application") version "9.0.0"
    kotlin("android") version "2.2.0"
}

android {
    namespace = "com.example.chineseocr"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.chineseocr"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.rmtheis:tess-two:9.1.0")
    implementation("org.apache.poi:poi-ooxml:5.4.1")

    testImplementation("junit:junit:4.13.2")
}

tasks.register<Copy>("exportDebugApkToRoot") {
    dependsOn("assembleDebug")
    from(layout.buildDirectory.file("outputs/apk/debug/app-debug.apk"))
    into(rootProject.layout.projectDirectory)
    rename { "ChineseOCR-debug.apk" }
}
