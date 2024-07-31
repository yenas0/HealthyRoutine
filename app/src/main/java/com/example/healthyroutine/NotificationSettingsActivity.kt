package com.example.healthyroutine

import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var switchAllNotifications: Switch
    private lateinit var switchRoutineNotifications: Switch
    private lateinit var switchPostNotifications: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        switchAllNotifications = findViewById(R.id.switchAllNotifications)
        switchRoutineNotifications = findViewById(R.id.switchRoutineNotifications)
        switchPostNotifications = findViewById(R.id.switchPostNotifications)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Load the current settings and update the switches accordingly
        loadSettings()

        // Set up the listeners to save settings when switches are toggled
        switchAllNotifications.setOnCheckedChangeListener { _, isChecked ->
            setAllSwitches(isChecked)
            saveSetting("all_notifications", isChecked)
        }

        switchRoutineNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("routine_notifications", isChecked)
            updateAllNotificationsSwitch()
        }

        switchPostNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("post_notifications", isChecked)
            updateAllNotificationsSwitch()
        }
    }

    private fun loadSettings() {
        // Replace with actual loading from shared preferences or database
        switchAllNotifications.isChecked = getSetting("all_notifications", true)
        switchRoutineNotifications.isChecked = getSetting("routine_notifications", true)
        switchPostNotifications.isChecked = getSetting("post_notifications", true)
    }

    private fun saveSetting(key: String, value: Boolean) {
        // Replace with actual saving to shared preferences or database
        val sharedPreferences = getSharedPreferences("notification_settings", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private fun getSetting(key: String, defaultValue: Boolean): Boolean {
        // Replace with actual loading from shared preferences or database
        val sharedPreferences = getSharedPreferences("notification_settings", MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    private fun setAllSwitches(isChecked: Boolean) {
        switchRoutineNotifications.setOnCheckedChangeListener(null)
        switchPostNotifications.setOnCheckedChangeListener(null)

        switchRoutineNotifications.isChecked = isChecked
        switchPostNotifications.isChecked = isChecked

        switchRoutineNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("routine_notifications", isChecked)
            updateAllNotificationsSwitch()
        }

        switchPostNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("post_notifications", isChecked)
            updateAllNotificationsSwitch()
        }
    }

    private fun updateAllNotificationsSwitch() {
        val allChecked = switchRoutineNotifications.isChecked && switchPostNotifications.isChecked
        switchAllNotifications.setOnCheckedChangeListener(null)
        switchAllNotifications.isChecked = allChecked
        switchAllNotifications.setOnCheckedChangeListener { _, isChecked ->
            setAllSwitches(isChecked)
            saveSetting("all_notifications", isChecked)
        }
    }
}
