plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.midterm.mobiledesignfinalterm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.midterm.mobiledesignfinalterm"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.transition:transition:1.4.1")

    implementation ("mysql:mysql-connector-java:8.0.28")

    implementation ("org.mindrot:jbcrypt:0.4")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    
    // Image loading with Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    
    // JSON parsing with Gson
    implementation ("com.google.code.gson:gson:2.10.1")
    
    // Network calls with Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // PDF generation with iTextPDF
    implementation ("com.itextpdf:itext7-core:7.2.5")
    
    // RecyclerView (if not already included)
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
}