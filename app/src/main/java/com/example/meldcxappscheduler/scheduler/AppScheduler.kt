package com.example.meldcxappscheduler.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
//
//class AppScheduler {
//
//    private val context: Context
//
//    constructor(context: Context) {
//        this.context = context
//    }
//
//    fun scheduleApp(packageName: String, startTime: Long) {
//        // Get the JobScheduler instance
//        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//
//        // Create a JobInfo object
//        val jobInfo = JobInfo.Builder(0, packageName)
//            .setRequiresCharging(true)
//            .setRequiresDeviceIdle(true)
//            .setTriggerContentUri(Uri.parse("content://com.example.appscheduler/schedule"))
//            .setMinimumLatency(startTime) // Start the job at the specified time
//            .build()
//
//        // Schedule the job
//        jobScheduler.schedule(jobInfo)
//
//        Toast.makeText(context, "App scheduled to start at $startTime", Toast.LENGTH_SHORT).show()
//    }
//
//    fun cancelSchedule(packageName: String) {
//        // Get the JobScheduler instance
//        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//
//        // Cancel the job
//        jobScheduler.cancel(packageName)
//
//        Toast.makeText(context, "App schedule canceled", Toast.LENGTH_SHORT).show()
//    }
//}


import com.example.meldcxappscheduler.receiver.AppLaunchReceiver

data class ScheduledApp(
    val packageName: String, // Package name of the app to be launched
    var scheduledTime: Long // Time at which the app should be launched (timestamp)
)

class AppScheduler(private val context: Context) {
    private val scheduledApps = mutableListOf<ScheduledApp>()
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleApp(packageName: String, scheduledTime: Long) {
        val appToReSchedule = scheduledApps.find { it.packageName == packageName }
        if (appToReSchedule != null) {
            rescheduleApp(packageName, scheduledTime)
        } else {
            val app = ScheduledApp(packageName, scheduledTime)
            scheduledApps.add(app)
            scheduleAppLaunch(app)
        }
    }

    fun cancelScheduledApp(packageName: String) {
        val appToRemove = scheduledApps.find { it.packageName == packageName }
        if (appToRemove != null) {
            removeScheduledApp(appToRemove)
        }
    }

    private fun removeScheduledApp(app: ScheduledApp) {
        val intent = Intent(context, AppLaunchReceiver::class.java)
        intent.action = app.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            alarmManager.cancel(pendingIntent)
        } else {
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        }
        scheduledApps.remove(app)
    }

    fun rescheduleApp(packageName: String, newScheduledTime: Long) {
        val appToReschedule = scheduledApps.find { it.packageName == packageName }
        if (appToReschedule != null) {
            removeScheduledApp(appToReschedule)
            appToReschedule.scheduledTime = newScheduledTime
            scheduleAppLaunch(appToReschedule)
        }
    }

    private fun scheduleAppLaunch(app: ScheduledApp) {
        val currentTime = System.currentTimeMillis()
        val delay = app.scheduledTime - currentTime
        if (delay > 0) {
            val intent = Intent(context, AppLaunchReceiver::class.java)
            intent.putExtra("packageName", app.packageName);
            intent.action = app.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, app.scheduledTime, pendingIntent)

            } else {
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, app.scheduledTime, pendingIntent)

            }
        } else {
            launchApp(app.packageName)
        }
    }

    private fun launchApp(packageName: String) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
