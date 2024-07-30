package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MyPageActivity : AppCompatActivity() {

    private lateinit var postsCountLayout: LinearLayout
    private lateinit var likedCountLayout: LinearLayout
    private lateinit var pointsCountLayout: LinearLayout
    private lateinit var logoutButton: Button
    private lateinit var settingsImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        postsCountLayout = findViewById(R.id.postsCountLayout)
        likedCountLayout = findViewById(R.id.likedCountLayout)
        pointsCountLayout = findViewById(R.id.pointsCountLayout)
        logoutButton = findViewById(R.id.logoutButton)
        settingsImageView = findViewById(R.id.settingsImageView)

        // 내가 쓴 글 영역 클릭 이벤트 설정
        postsCountLayout.setOnClickListener {
            val intent = Intent(this, MyPostsActivity::class.java)
            startActivity(intent)
        }

        // 좋아요한 글 영역 클릭 이벤트 설정
        likedCountLayout.setOnClickListener {
            val intent = Intent(this, LikedPostsActivity::class.java)
            startActivity(intent)
        }

        // 나의 포인트 영역 클릭 이벤트 설정
        pointsCountLayout.setOnClickListener{
//            val intent = Intent(this, RankingActivity::class.java)
//            startActivity(intent)
        }

        // 로그아웃 버튼 클릭 이벤트 설정
        logoutButton.setOnClickListener {
            // 로그아웃 처리 로직
        }

        // 설정 클릭 이벤트 설정
        settingsImageView.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }
}
