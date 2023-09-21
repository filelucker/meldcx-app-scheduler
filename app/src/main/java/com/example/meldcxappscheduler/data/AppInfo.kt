package com.example.meldcxappscheduler.data

import android.graphics.drawable.Drawable

// Created by @author Moniruzzaman on 18/9/23. github: filelucker

data class AppInfo(
    val id: Int,
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val scheduleTime: String
)
