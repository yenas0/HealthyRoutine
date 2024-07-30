package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MyPageActivity : AppCompatActivity() {

    private lateinit var postsCountLayout: LinearLayout
    private lateinit var likedCountLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        postsCountLayout = findViewById(R.id.postsCountLayout)
        likedCountLayout = findViewById(R.id.likedCountLayout)

        // postsCountLayout과 LikedCountLayout 클릭 이벤트 설정
        postsCountLayout.setOnClickListener {
            val intent = Intent(this, MyPostsActivity::class.java)
            startActivity(intent)
        }

        likedCountLayout.setOnClickListener {
            val intent = Intent(this, LikedPostsActivity::class.java)
            startActivity(intent)
        }

    }
}
