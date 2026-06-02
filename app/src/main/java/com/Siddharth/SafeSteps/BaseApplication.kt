package com.Siddharth.SafeSteps

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.loadKoinModules

const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "Channel Name"

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@BaseApplication)
            loadKoinModules(com.Siddharth.SafeSteps.di.appModule)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}