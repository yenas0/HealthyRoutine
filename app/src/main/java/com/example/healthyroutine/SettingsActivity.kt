package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton: View = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    fun onEditProfileClick(view: View) {
        // 회원 정보 수정 화면으로 이동
         val intent = Intent(this, EditProfileActivity::class.java)
         startActivity(intent)
    }

    fun onNotificationSettingsClick(view: View) {
        // 알림 설정 화면으로 이동
         val intent = Intent(this, NotificationSettingsActivity::class.java)
         startActivity(intent)
    }

    fun onDeleteAccountClick(view: View) {
        // 회원 탈퇴 화면으로 이동
        // val intent = Intent(this, DeleteAccountActivity::class.java)
        // startActivity(intent)
    }
}
