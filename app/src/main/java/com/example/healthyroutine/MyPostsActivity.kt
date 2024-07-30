package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView
import com.example.healthyroutine.DatabaseHelper

class MyPostsActivity : AppCompatActivity() {

    lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        dbHelper = DatabaseHelper(this)

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadMyPosts()
    }

    private fun loadMyPosts() {
        val postsContainer: LinearLayout = findViewById(R.id.my_posts_container)
        postsContainer.removeAllViews()

        val posts = dbHelper.getMyPosts() // 이 함수는 사용자가 작성한 글을 가져옵니다.
        for (post in posts) {
            val postView = layoutInflater.inflate(R.layout.post_item, null)

            val titleTextView: TextView = postView.findViewById(R.id.post_title)
            val contentTextView: TextView = postView.findViewById(R.id.post_content)
            val likesTextView: TextView = postView.findViewById(R.id.post_likes)
            val likeIcon: ImageView = postView.findViewById(R.id.post_like_icon)

            titleTextView.text = post.title
            contentTextView.text = post.content
            likesTextView.text = post.likes.toString()

            postsContainer.addView(postView)

            // 구분선 추가
            val divider = View(this)
            divider.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ).apply {
                setMargins(0, 16, 0, 16)
            }
            divider.setBackgroundColor(resources.getColor(R.color.gray))
            postsContainer.addView(divider)
        }
    }
}
