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

class BoardActivity : AppCompatActivity() {

    lateinit var bottom_navigation: BottomNavigationView
    lateinit var btn_add_post: ImageButton
    lateinit var firestoreHelper: FirestoreHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        bottom_navigation = findViewById(R.id.bottom_navigation)
        btn_add_post = findViewById(R.id.btn_add_post)
        firestoreHelper = FirestoreHelper()

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

        // loadPosts를 onCreate가 아닌 onResume에서 호출
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
        loadPopularPosts()
    }

    private fun loadPosts() {
        val postsContainer: LinearLayout = findViewById(R.id.posts_container)
        postsContainer.removeAllViews() // 기존 게시글 리스트 초기화

        firestoreHelper.getAllPosts { posts ->
            postsContainer.removeAllViews() // 불필요한 초기화를 다시 한 번 확인

            for (post in posts) {
                val postView = layoutInflater.inflate(R.layout.post_item, null)

                val titleTextView: TextView = postView.findViewById(R.id.post_title)
                val contentTextView: TextView = postView.findViewById(R.id.post_content)
                val likesTextView: TextView = postView.findViewById(R.id.post_likes)
                val likeIcon: ImageView = postView.findViewById(R.id.post_like_icon)

                titleTextView.text = post.title
                contentTextView.text = post.content
                likesTextView.text = post.likes.toString()

                var isLiked = false
                firestoreHelper.isLiked(post.id, post.userId) { liked ->
                    isLiked = liked
                    likeIcon.setImageResource(if (liked) R.drawable.ic_like_filled else R.drawable.ic_like_empty)
                }

                likeIcon.setOnClickListener {
                    if (isLiked) {
                        post.likes -= 1
                        likeIcon.setImageResource(R.drawable.ic_like_empty)
                        firestoreHelper.removeLike(post.id, post.userId)
                    } else {
                        post.likes += 1
                        likeIcon.setImageResource(R.drawable.ic_like_filled)
                        firestoreHelper.addLike(post.id, post.userId)
                    }
                    isLiked = !isLiked
                    firestoreHelper.updatePost(post)
                    likesTextView.text = post.likes.toString()
                    loadPopularPosts() // 좋아요 변경 시 실시간 인기글 업데이트
                }

                postView.setOnClickListener {
                    val intent = Intent(this, RoutineAddActivity::class.java)
                    intent.putExtra("post_id", post.id)
                    intent.putExtra("title", post.title)
                    intent.putExtra("content", post.content)
                    if (post.routine != null) {
                        intent.putExtra("routine", post.routine)
                        intent.putExtra("routine_days", post.routineDays)
                        Toast.makeText(this, "게시글에 설정된 루틴이 있습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "기본 루틴 추가 창입니다.", Toast.LENGTH_SHORT).show()
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
        popularPostContainer.removeAllViews() // 기존 인기 게시글 리스트 초기화

        firestoreHelper.getPopularPosts { popularPosts ->
            popularPostContainer.removeAllViews() // 불필요한 초기화를 다시 한 번 확인

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

                    var isLiked = false
                    firestoreHelper.isLiked(post.id, post.userId) { liked ->
                        isLiked = liked
                        likeIcon.setImageResource(if (liked) R.drawable.ic_like_filled else R.drawable.ic_like_empty)
                    }

                    likeIcon.setOnClickListener {
                        if (isLiked) {
                            post.likes -= 1
                            likeIcon.setImageResource(R.drawable.ic_like_empty)
                            firestoreHelper.removeLike(post.id, post.userId)
                        } else {
                            post.likes += 1
                            likeIcon.setImageResource(R.drawable.ic_like_filled)
                            firestoreHelper.addLike(post.id, post.userId)
                        }
                        isLiked = !isLiked
                        firestoreHelper.updatePost(post)
                        likesTextView.text = post.likes.toString()
                        loadPopularPosts() // 좋아요 변경 시 실시간 인기글 업데이트
                    }

                    postView.setOnClickListener {
                        val intent = Intent(this, RoutineAddActivity::class.java)
                        intent.putExtra("post_id", post.id)
                        intent.putExtra("title", post.title)
                        intent.putExtra("content", post.content)
                        if (post.routine != null) {
                            intent.putExtra("routine", post.routine)
                            intent.putExtra("routine_days", post.routineDays)
                            Toast.makeText(this, "게시글에 설정된 루틴이 있습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "기본 루틴 추가 창입니다.", Toast.LENGTH_SHORT).show()
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
