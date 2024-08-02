package com.example.healthyroutine

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LikedPostsActivity : AppCompatActivity() {

    private lateinit var firestoreHelper: FirestoreHelper
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_posts)

        firestoreHelper = FirestoreHelper()
        auth = FirebaseAuth.getInstance()

        loadLikedPosts()

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadLikedPosts() {
        val postsContainer: LinearLayout = findViewById(R.id.liked_posts_container)
        postsContainer.removeAllViews()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestoreHelper.getLikedPosts(currentUser.uid) { posts ->
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
                    firestoreHelper.isLiked(post.id, currentUser.uid) { liked ->
                        isLiked = liked
                        likeIcon.setImageResource(if (liked) R.drawable.ic_like_filled else R.drawable.ic_like_empty)
                    }

                    likeIcon.setOnClickListener {
                        if (isLiked) {
                            post.likes -= 1
                            likeIcon.setImageResource(R.drawable.ic_like_empty)
                            firestoreHelper.removeLike(post.id, currentUser.uid)
                        } else {
                            post.likes += 1
                            likeIcon.setImageResource(R.drawable.ic_like_filled)
                            firestoreHelper.addLike(post.id, currentUser.uid)
                        }
                        isLiked = !isLiked
                        firestoreHelper.updatePost(post)
                        likesTextView.text = post.likes.toString()
                    }

                    postsContainer.addView(postView)

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
    }
}
