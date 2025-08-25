androidApplication {
    namespace = "org.example.notes"

    dependencies {
        // AndroidX Core and AppCompat
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.7.0")

        // Material components
        implementation("com.google.android.material:material:1.12.0")

        // Lifecycle + ViewModel
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

        // RecyclerView
        implementation("androidx.recyclerview:recyclerview:1.3.2")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    }
}
