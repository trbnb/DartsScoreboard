plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "de.trbnb.darts"
        minSdkVersion(30)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
        }

    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("de.trbnb.mvvmbase:mvvmbase:2.1.0")
    implementation("de.trbnb.mvvmbase:mvvmbaseCoroutines:2.1.0")

    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    implementation("com.google.dagger:hilt-android:2.31-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.31-alpha")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha03")

    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.activity:activity-compose:1.3.0-alpha03")
    implementation("androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    implementation("androidx.compose.ui:ui:1.0.0-beta01")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.0.0-beta01")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0-beta01")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-beta01")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.0-beta01")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta01")
    // Integration with activities
    implementation("androidx.activity:activity-compose:1.3.0-alpha03")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-beta01")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0-beta01")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0-beta01")


    implementation("androidx.room:room-runtime:${Versions.room}")
    kapt("androidx.room:room-compiler:${Versions.room}")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:${Versions.room}")

    implementation("com.github.madrapps:pikolo:2.0.1")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    implementation("androidx.fragment:fragment-ktx:1.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.3")
}