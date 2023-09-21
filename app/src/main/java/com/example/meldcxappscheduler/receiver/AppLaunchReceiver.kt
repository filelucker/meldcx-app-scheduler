package com.example.meldcxappscheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.meldcxappscheduler.data.database.ScheduleDatabase
import com.example.meldcxappscheduler.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppLaunchReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AppLaunchReceiver", "broadcast received")
        val packageName: String = intent?.getStringExtra("packageName").toString()
        Log.d("AppLaunchReceiver", packageName)
        if (context != null) {
            if (packageName != null) {
                launchApp(context, packageName)
            } else {
                Log.d("AppLaunchReceiver", "packageName == null")
            }
        } else {
            Log.d("AppLaunchReceiver", "context == null")
        }
    }

    private fun launchApp(context: Context, packageName: String) {
        try {
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(packageName)

            if (intent != null) {
                intent.addCategory("android.intent.category.LAUNCHER")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                Log.d("AppLaunchReceiver", "intent == null")
            }

            CoroutineScope(Dispatchers.IO).launch {
                val dao = ScheduleDatabase.getDatabase(context).appDao()
                dao.deleteByPackageId(packageName)
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }
}
