plugins {
    id("com.android.application")
}

android {
    namespace = "ai.j0b"
    compileSdk = 34

    defaultConfig {
        applicationId = "ai.j0b"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        dataBinding = true
    }

    packaging {
        resources {
            excludes.add("META-INF/io.netty.versions.properties")
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/DEPENDENCIES")
        }
    }
}

dependencies {
    // Existing dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //implementation(project(mapOf("path" to ":ads")))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.wdullaer:materialdatetimepicker:4.2.3")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.otaliastudios:zoomlayout:1.9.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // New dependencies
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.apache.xmlbeans:xmlbeans:5.2.0")
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    implementation("net.sf.saxon:Saxon-HE:12.4")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.karumi:dexter:6.2.3")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }
}
