// MyApplication.kt – initialize encrypted DB repository
package com.cybercoretech.hyprsync

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialise the ContactRepository with application context
        ContactRepository.init(this)
    }
}
