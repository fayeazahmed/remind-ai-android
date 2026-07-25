package com.ahmed.remindai

import android.app.Application
import com.ahmed.remindai.network.RetrofitInstance
import kotlinx.coroutines.runBlocking

class RemindAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.init(this)
        runBlocking {
            RetrofitInstance.tokenManager.getInitialToken()
        }
    }
}
