package com.shale.nammapride

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class ShaleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            // Using your project: gen-lang-client-0604218984
            val options = FirebaseOptions.Builder()
                .setProjectId("gen-lang-client-0604218984")
                .setApplicationId("1:11197316403:android:19ae3440cd63a715c3d246")
                .setApiKey("AIzaSyA281oPds-xc9tPXUj-FAMKY5rcqXgKIWc")
                .setStorageBucket("gen-lang-client-0604218984.firebasestorage.app")
                .build()

            FirebaseApp.initializeApp(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
