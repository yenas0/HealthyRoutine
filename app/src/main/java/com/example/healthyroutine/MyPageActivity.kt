package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MyPageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firestoreHelper: FirestoreHelper
    private var currentUser: FirebaseUser? = null

    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var postsCountTextView: TextView
    private lateinit var likedCountTextView: TextView
    private lateinit var pointsTextView: TextView

    private lateinit var postsCountLayout: LinearLayout
    private lateinit var likedCountLayout: LinearLayout
    private lateinit var pointsCountLayout: LinearLayout
    private lateinit var logoutButton: Button
    private lateinit var settingsImageView: ImageView

    lateinit var bottom_navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firestoreHelper = FirestoreHelper()
        currentUser = auth.currentUser

        profileImageView = findViewById(R.id.profileImageView)
        usernameTextView = findViewById(R.id.usernameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        postsCountTextView = findViewById(R.id.postsCount)
        likedCountTextView = findViewById(R.id.likedCount)
        pointsTextView = findViewById(R.id.pointsCount)

        postsCountLayout = findViewById(R.id.postsCountLayout)
        likedCountLayout = findViewById(R.id.likedCountLayout)
        pointsCountLayout = findViewById(R.id.pointsCountLayout)
        logoutButton = findViewById(R.id.logoutButton)
        settingsImageView = findViewById(R.id.settingsImageView)

        postsCountLayout.setOnClickListener {
            val intent = Intent(this, MyPostsActivity::class.java)
            startActivity(intent)
        }

        likedCountLayout.setOnClickListener {
            val intent = Intent(this, LikedPostsActivity::class.java)
            startActivity(intent)
        }

        pointsCountLayout.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        settingsImageView.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        bottom_navigation = findViewById(R.id.bottom_navigation)
        bottom_navigation.selectedItemId = R.id.navigation_profile

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
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
        updatePoints()
    }

    private fun loadUserProfile() {
        val user = currentUser
        if (user != null) {
            emailTextView.text = user.email
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nickname = document.getString("nickname")
                        val profileImageUrl = document.getString("profileImageUrl")

                        usernameTextView.text = nickname

                        profileImageUrl?.let {
                            Glide.with(this)
                                .load(it)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageView)
                        }

                        updatePostCounts(user.uid)
                    } else {
                        Log.w("MyPageActivity", "No such document")
                        Toast.makeText(this, "프로필 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("MyPageActivity", "Error getting documents: ", exception)
                    Toast.makeText(this, "프로필 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePostCounts(userId: String) {
        firestore.collection("posts").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                val postsCount = result.size()
                postsCountTextView.text = postsCount.toString()
            }

        firestore.collection("posts").get().addOnSuccessListener { result ->
            var likedCount = 0
            for (document in result) {
                document.reference.collection("likes").document(userId).get().addOnSuccessListener { likeDoc ->
                    if (likeDoc.exists()) {
                        likedCount++
                        likedCountTextView.text = likedCount.toString()
                    }
                }
            }
        }
    }

    private fun updatePoints() {
        val user = currentUser
        if (user != null) {
            firestoreHelper.getUserPoints(user.uid) { points ->
                pointsTextView.text = "${points}p"
            }
        } else {
            pointsTextView.text = "0p"
        }
    }
}
