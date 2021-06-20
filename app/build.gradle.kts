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
        useLiveLiterals = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("de.trbnb:mvvmbase-core:3.0.0-alpha01")
    implementation("de.trbnb:mvvmbase-coroutines:3.0.0-alpha01")
    implementation(kotlin("reflect"))
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    debugImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    implementation("com.google.dagger:hilt-android:${Versions.daggerHilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0-beta01")
    // Hilt integration
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha01")

    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.activity:activity-compose:1.3.0-alpha06")
    implementation("androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    implementation("androidx.compose.ui:ui:${Versions.compose}")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:${Versions.compose}")
    // Material Design
    implementation("androidx.compose.material:material:${Versions.compose}")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:${Versions.compose}")
    implementation("androidx.compose.material:material-icons-extended:${Versions.compose}")
    // Integration with activities
    implementation("androidx.activity:activity-compose:1.3.0-alpha06")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha04")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.compose}")
    implementation("androidx.compose.runtime:runtime-rxjava2:${Versions.compose}")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")


    implementation("androidx.room:room-runtime:${Versions.room}")
    kapt("androidx.room:room-compiler:${Versions.room}")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:${Versions.room}")

    implementation("com.github.madrapps:pikolo:2.0.1")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.fragment:fragment-ktx:1.3.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
}