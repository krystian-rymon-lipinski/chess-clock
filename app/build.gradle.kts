import org.jetbrains.kotlin.gradle.plugin.extraProperties
import java.io.FileInputStream
import java.util.Properties

plugins{
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    signingConfigs {
        create("release") {
            val keystorePropertiesFile: File = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            storeFile = file(keystoreProperties["keystorePath"].toString())
            keyAlias = keystoreProperties["keyAlias"].toString()
            storePassword = keystoreProperties["keystorePassword"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
        }
    }

    namespace = "com.krystianrymonlipinski.chessclock"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.krystianrymonlipinski.chessclock"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val hiltVersion = rootProject.extraProperties.get("hiltVersion")
    val roomVersion = "2.6.1"
    val coroutinesVersion = "1.7.3"

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")

}
