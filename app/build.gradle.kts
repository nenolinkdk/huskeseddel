import com.android.build.api.artifact.SingleArtifact
import java.io.File

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.nenolink.huskeseddel"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nenolink.huskeseddel"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0-dev"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { compose = true }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

base {
    archivesName.set("Huskeseddel")
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val capitalized = variant.name.replaceFirstChar { it.uppercaseChar() }
        val apkFolder = variant.artifacts.get(SingleArtifact.APK)
        val artifactsLoader = variant.artifacts.getBuiltArtifactsLoader()
        val copyTask = tasks.register("copyNamed${capitalized}Apk") {
            description = "Copies the release APK to Huskeseddel.apk"
            inputs.files(apkFolder)
            val output = layout.buildDirectory.file("outputs/apk/release/Huskeseddel.apk")
            outputs.file(output)
            doLast {
                val built = artifactsLoader.load(apkFolder.get()) ?: error("Cannot load APKs")
                val source = File(built.elements.single().outputFile)
                val target = output.get().asFile
                target.parentFile.mkdirs()
                source.copyTo(target, overwrite = true)
            }
        }
        tasks.matching { it.name == "assemble$capitalized" }.configureEach {
            finalizedBy(copyTask)
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("androidx.room:room-runtime:2.8.1")
    implementation("androidx.room:room-ktx:2.8.1")
    ksp("androidx.room:room-compiler:2.8.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20250517")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
