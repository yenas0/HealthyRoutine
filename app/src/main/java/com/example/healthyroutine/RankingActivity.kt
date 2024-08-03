package com.example.healthyroutine

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class RankingActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: RankingAdapter
    private lateinit var firestoreHelper: FirestoreHelper
    private lateinit var userList: ArrayList<User>

    // 상위 3명 사용자
    private lateinit var user1Image: ImageView
    private lateinit var user1Name: TextView
    private lateinit var user1Points: TextView
    private lateinit var user2Image: ImageView
    private lateinit var user2Name: TextView
    private lateinit var user2Points: TextView
    private lateinit var user3Image: ImageView
    private lateinit var user3Name: TextView
    private lateinit var user3Points: TextView

    // 나의 포인트
    private lateinit var myPointsTextView: TextView

    lateinit var bottom_navigation: BottomNavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        firestoreHelper = FirestoreHelper()  // FirestoreHelper 초기화

        mRecyclerView = findViewById(R.id.ranking_recyclerView)

        // initiate adapter
        mRecyclerAdapter = RankingAdapter()

        // initiate recyclerview
        mRecyclerView.adapter = mRecyclerAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        user1Image = findViewById(R.id.user1_image)
        user1Name = findViewById(R.id.user1_name)
        user1Points = findViewById(R.id.user1_points)
        user2Image = findViewById(R.id.user2_image)
        user2Name = findViewById(R.id.user2_name)
        user2Points = findViewById(R.id.user2_points)
        user3Image = findViewById(R.id.user3_image)
        user3Name = findViewById(R.id.user3_name)
        user3Points = findViewById(R.id.user3_points)

        myPointsTextView = findViewById(R.id.my_points)

        // Firestore에서 데이터 가져오기
        fetchRankingData()

        bottom_navigation = findViewById(R.id.bottom_navigation)
        bottom_navigation.selectedItemId = R.id.navigation_ranking

        // BottomNavigationView 설정
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
                    // 현재 액티비티와 동일하므로 추가적인 동작 없음
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

    private fun fetchRankingData() {
        firestoreHelper.getUsersOrderedByPoints { users ->
            userList = ArrayList(users)

            // 상위 3명 사용자 정보 업데이트
            updateTopThreeUsers()

            // 4위부터 사용자 정보
            val remainingUsers = userList.drop(3).mapIndexed { index, user ->
                RankingUserActivity(
                    ranking = index + 4, // 4위부터 시작
                    name = user.name,
                    points = user.points,
                    profileImageUrl = user.profileImageUrl // 프로필 이미지 URL 설정
                )
            }
            mRecyclerAdapter.setUserList(ArrayList(remainingUsers))
        }
    }


    private fun updateTopThreeUsers() {
        // 초기화: 사용자 정보가 없는 경우 기본값 설정
        user1Name.text = ""
        user1Points.text = "0p"
        user2Name.text = ""
        user2Points.text = "0p"
        user3Name.text = ""
        user3Points.text = "0p"

        if (userList.size >= 1) {
            val user1 = userList[0]
            Glide.with(this)
                .load(user1.profileImageUrl)
                .into(user1Image)
            user1Name.text = user1.name
            user1Points.text = "${user1.points}p"
        }
        if (userList.size >= 2) {
            val user2 = userList[1]
            Glide.with(this)
                .load(user2.profileImageUrl)
                .into(user2Image)
            user2Name.text = user2.name
            user2Points.text = "${user2.points}p"
        }
        if (userList.size >= 3) {
            val user3 = userList[2]
            Glide.with(this)
                .load(user3.profileImageUrl)
                .into(user3Image)
            user3Name.text = user3.name
            user3Points.text = "${user3.points}p"
        }
    }

    private fun updateMyPoints() {
        val myUserId = "your_user_id"  // 실제 로그인된 사용자 ID를 사용
        firestoreHelper.getUserPoints(myUserId) { points ->
            myPointsTextView.text = "${points}p"
        }
    }
}