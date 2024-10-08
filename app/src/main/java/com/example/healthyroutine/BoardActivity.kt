package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class BoardActivity : AppCompatActivity() {

    lateinit var bottom_navigation: BottomNavigationView
    lateinit var btn_add_post: ImageButton
    lateinit var firestoreHelper: FirestoreHelper
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        bottom_navigation = findViewById(R.id.bottom_navigation)
        btn_add_post = findViewById(R.id.btn_add_post)
        firestoreHelper = FirestoreHelper()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        bottom_navigation.selectedItemId = R.id.navigation_board

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_home).isChecked = true
                    true
                }
                R.id.navigation_recommend -> {
                    val intent = Intent(this, RecommendActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_recommend).isChecked = true
                    true
                }
                R.id.navigation_board -> {
                    val intent = Intent(this, BoardActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_board).isChecked = true
                    true
                }
                R.id.navigation_ranking -> {
                    val intent = Intent(this, RankingActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_ranking).isChecked = true
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_profile).isChecked = true
                    true
                }
                else -> false
            }
        }

        btn_add_post.setOnClickListener {
            val intent = Intent(this, PostWriteActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
        loadPopularPosts()
    }

    private fun loadPosts() {
        val postsContainer: LinearLayout = findViewById(R.id.posts_container)
        postsContainer.removeAllViews()

        firestoreHelper.getAllPosts { posts ->
            for (post in posts) {
                val postView = layoutInflater.inflate(R.layout.post_item, null)

                val titleTextView: TextView = postView.findViewById(R.id.post_title)
                val contentTextView: TextView = postView.findViewById(R.id.post_content)
                val likesTextView: TextView = postView.findViewById(R.id.post_likes)
                val likeIcon: ImageView = postView.findViewById(R.id.post_like_icon)

                titleTextView.text = post.title
                contentTextView.text = post.content
                likesTextView.text = post.likes.toString()

                firestoreHelper.isLiked(post.id, currentUser.uid) { isLiked ->
                    likeIcon.setImageResource(if (isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_empty)
                }

                likeIcon.setOnClickListener {
                    firestoreHelper.isLiked(post.id, currentUser.uid) { isLiked ->
                        if (isLiked) {
                            firestoreHelper.removeLike(post.id, currentUser.uid)
                            post.likes -= 1
                            likeIcon.setImageResource(R.drawable.ic_like_empty)
                        } else {
                            firestoreHelper.addLike(post.id, currentUser.uid)
                            post.likes += 1
                            likeIcon.setImageResource(R.drawable.ic_like_filled)
                        }
                        likesTextView.text = post.likes.toString()
                        firestoreHelper.updatePost(post)
                        loadPopularPosts()  // 좋아요 변경 시 실시간 인기글 업데이트
                    }
                }

                postView.setOnClickListener {
                    val intent = Intent(this, RoutineAddActivity::class.java).apply {
                        putExtra("post_id", post.id)
                        putExtra("title", post.title)
                        putExtra("content", post.content)
                        putExtra("SOURCE", "BOARD")  // SOURCE 추가
                        if (post.routine != null) {
                            putExtra("routine_name", post.routine)
                            putExtra("routine_days", post.routineDays)
                            Toast.makeText(this@BoardActivity, "게시글에 설정된 루틴이 있습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@BoardActivity, "기본 루틴 추가 창입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    startActivity(intent)
                }

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

    private fun loadPopularPosts() {
        val popularPostContainer: LinearLayout = findViewById(R.id.popular_post_container)
        popularPostContainer.removeAllViews()

        firestoreHelper.getPopularPosts { popularPosts ->
            if (popularPosts.isEmpty()) {
                val noPopularPostsView = layoutInflater.inflate(R.layout.no_popular_posts_item, null)
                popularPostContainer.addView(noPopularPostsView)
            } else {
                for (post in popularPosts) {
                    val postView = layoutInflater.inflate(R.layout.post_item, null)

                    val titleTextView: TextView = postView.findViewById(R.id.post_title)
                    val contentTextView: TextView = postView.findViewById(R.id.post_content)
                    val likesTextView: TextView = postView.findViewById(R.id.post_likes)
                    val likeIcon: ImageView = postView.findViewById(R.id.post_like_icon)

                    titleTextView.text = post.title
                    contentTextView.text = post.content
                    likesTextView.text = post.likes.toString()

                    firestoreHelper.isLiked(post.id, currentUser.uid) { isLiked ->
                        likeIcon.setImageResource(if (isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_empty)
                    }

                    likeIcon.setOnClickListener {
                        firestoreHelper.isLiked(post.id, currentUser.uid) { isLiked ->
                            if (isLiked) {
                                firestoreHelper.removeLike(post.id, currentUser.uid)
                                post.likes -= 1
                                likeIcon.setImageResource(R.drawable.ic_like_empty)
                            } else {
                                firestoreHelper.addLike(post.id, currentUser.uid)
                                post.likes += 1
                                likeIcon.setImageResource(R.drawable.ic_like_filled)
                            }
                            likesTextView.text = post.likes.toString()
                            firestoreHelper.updatePost(post)
                            loadPopularPosts()  // 좋아요 변경 시 실시간 인기글 업데이트
                        }
                    }

                    postView.setOnClickListener {
                        val intent = Intent(this, RoutineAddActivity::class.java).apply {
                            putExtra("SOURCE", "BOARD")
                            putExtra("routine_name", post.routine)
                            putExtra("routine_days", post.routineDays)
                        }
                        startActivity(intent)
                    }

                    popularPostContainer.addView(postView)

                    // 구분선 추가
                    val divider = View(this)
                    divider.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        setMargins(0, 16, 0, 16)
                    }
                    divider.setBackgroundColor(resources.getColor(R.color.gray))
                    popularPostContainer.addView(divider)
                }
            }
        }
    }
}
