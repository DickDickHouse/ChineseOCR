import org.jetbrains.kotlin.gradle.tasks.KotlinCompile  // ← 必須加入這個 import

plugins {
    id("com.android.application") version "9.0.1"
    // 🔸 如果專案用 Kotlin 開發，建議確認 project-level build.gradle.kts 有套用 kotlin("android")
    // 如果還沒加，可以在這裡補上（需確保 plugin 版本已定義）：
    // id("org.jetbrains.kotlin.android") version "1.9.20"
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // ❌ 移除這裡的 kotlinOptions { ... }，移到檔案底部處理

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

// ✅ 正確寫法：kotlinOptions 放在 android { } 區塊「外面」
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}                "META-INF/LICENSE.txt",
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
