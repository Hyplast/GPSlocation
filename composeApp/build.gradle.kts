import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    kotlin("kapt")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.play.services.location)
            implementation(libs.ktor.client.okhttp)
            //implementation(libs.glide.compose)


        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(compose.animation)

            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)
            api(libs.moko.permissions)
            api(libs.moko.permissions.compose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.core)

            api(libs.datastore.preferences)
            api(libs.datastore)

            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)
            implementation(libs.accompanist.permissions)

            //implementation(libs.glide.compose)
            //implementation("com.github.skydoves:landscapist-glide:2.4.7")
            implementation(libs.xmlutil)

            implementation(libs.kotlinx.datetime)

            implementation(libs.vico.compose)
            implementation(libs.vico.compose.m3)
            implementation(libs.vico.multiplatform)
            implementation(libs.vico.multiplatform.m3)

            implementation(libs.napier)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(kotlin("test-annotations-common"))
            implementation(libs.assertk)

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        dependencies {
            ksp(libs.androidx.room.compiler)
        }
    }
}

android {
    namespace = "fi.infinitygrow.gpslocation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "fi.infinitygrow.gpslocation"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "1.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // <--- This is the file you'd be looking for
            )
            signingConfig = signingConfigs.getByName("debug")
            ndk {
                // Choose one of the following levels:
                debugSymbolLevel = "SYMBOL_TABLE" // Only function names are retained.
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

