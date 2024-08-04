package com.example.healthyroutine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val routineName = intent.getStringExtra("routine_name") ?: return
        val notificationHelper = NotificationHelper(context)
        notificationHelper.sendNotification("Routine Reminder", "It's time for your routine: $routineName")
    }
}
