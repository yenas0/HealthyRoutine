package com.example.healthyroutine

import android.annotation.SuppressLint
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
        pointsCountLayout.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼 클릭 이벤트 설정
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 설정 클릭 이벤트 설정
        settingsImageView.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // 포인트 업데이트
        pointsTextView.text = "${PointsManager.getPoints()}p"

        bottom_navigation = findViewById(R.id.bottom_navigation)

        bottom_navigation.selectedItemId = R.id.navigation_profile

        // BottomNavigationView 설정
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // 홈 화면으로 이동
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_home).isChecked = true
                    true
                }
                R.id.navigation_recommend -> {
                    // 추천 화면으로 이동
                    val intent = Intent(this, RecommendActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_recommend).isChecked = true
                    true
                }
                R.id.navigation_board -> {
                    // 게시판 화면으로 이동
                    val intent = Intent(this, BoardActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_board).isChecked = true
                    true
                }
                R.id.navigation_ranking -> {
                    // 랭킹 화면으로 이동
                    val intent = Intent(this, RankingActivity::class.java)
                    startActivity(intent)
                    bottom_navigation.menu.findItem(R.id.navigation_ranking).isChecked = true
                    true
                }
                R.id.navigation_profile -> {
                    // 마이페이지 화면으로 이동
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

                        updatePostCounts()
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

    private fun updatePostCounts() {
        // 게시물 수 및 좋아요 수 업데이트를 위한 메소드 내용 제거
    }
}
