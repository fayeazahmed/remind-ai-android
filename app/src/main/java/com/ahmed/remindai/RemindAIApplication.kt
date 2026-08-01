package com.ahmed.remindai

import android.app.Application
import android.content.Context
import com.ahmed.remindai.network.RetrofitInstance
import kotlinx.coroutines.runBlocking

class RemindAIApplication : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        RetrofitInstance.init(this)

        runBlocking {
            RetrofitInstance.tokenManager.getInitialToken()
        }
    }
}
