import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp")
}

val apikeyPropertiesFile = rootProject.file("apikey.properties")
val apikeyProperties = Properties()
if (apikeyPropertiesFile.exists()) {
    apikeyPropertiesFile.inputStream().use { input ->
        apikeyProperties.load(input)
    }
}

android {
    namespace = "com.example.githubprofile"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.githubprofile"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        val rawToken = apikeyProperties.getProperty("ACCESS_TOKEN") ?: ""
        val accessToken = rawToken.trim('"')
        buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)


    //library untuk menampilkan gambar bergerak splashscreen
    implementation("com.airbnb.android:lottie:5.2.0")
    //library untuk menampilkan gambar bulat (circle)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    //library untuk menampilkan gambar melalui URL
    ksp("androidx.room:room-compiler:2.5.0") //menggunakan ksp lebih cepat dibandingkan kapt
    implementation("com.github.bumptech.glide:glide:4.13.2")
    //library untuk request API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //library untuk logging hasil request API
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.9")
}