package com.example.healthyroutine

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val updateProfileButton: Button = findViewById(R.id.updateProfileButton)
        updateProfileButton.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val currentPasswordEditText: EditText = findViewById(R.id.currentPasswordEditText)
        val newPasswordEditText: EditText = findViewById(R.id.newPasswordEditText)
        val confirmNewPasswordEditText: EditText = findViewById(R.id.confirmNewPasswordEditText)
        val nicknameEditText: EditText = findViewById(R.id.nicknameEditText)

        val currentPassword = currentPasswordEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()
        val confirmNewPassword = confirmNewPasswordEditText.text.toString()
        val nickname = nicknameEditText.text.toString()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty() || nickname.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmNewPassword) {
            Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 여기에 사용자 프로필 업데이트 로직 추가
        // 예: 데이터베이스에 업데이트, API 호출 등

        Toast.makeText(this, "회원 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }
}
