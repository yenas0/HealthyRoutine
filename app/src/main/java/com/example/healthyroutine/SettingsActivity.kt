package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

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
        // 회원 탈퇴 다이얼로그 호출
        showDeleteAccountDialog()
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(this)
            .setTitle("회원 탈퇴")
            .setMessage("정말 탈퇴하시겠습니까?")
            .setPositiveButton("탈퇴하기") { dialog, which ->
                deleteAccount()
            }
            .setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 계정 삭제 성공
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 계정 삭제 실패
                    AlertDialog.Builder(this)
                        .setTitle("오류")
                        .setMessage("계정 삭제에 실패했습니다. 다시 시도해주세요.")
                        .setPositiveButton("확인") { dialog, which ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }
    }

}
