package com.example.healthyroutine

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class RankingActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: RankingAdapter
    private lateinit var mRankerItems: ArrayList<RankingUserActivity>
    private lateinit var databaseHelper: DatabaseHelper

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

        databaseHelper = DatabaseHelper(this)  // SQLite DatabaseHelper 초기화

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

        // SQLite에서 데이터 가져오기
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

    private fun fetchRankingData() {
        mRankerItems = ArrayList()

        // SQLite 데이터베이스에서 모든 유저의 포인트 데이터를 가져와 랭킹을 만듭니다.
        val rankings = databaseHelper.getRankingUsers()

        mRankerItems.addAll(rankings)

        // 상위 3명 사용자 정보 업데이트
        updateTopThreeUsers()

        // 나의 포인트 가져오기
        updateMyPoints()

        // 4위부터 사용자 정보
        val remainingUsers = mRankerItems.drop(3)
        mRecyclerAdapter.setUserList(ArrayList(remainingUsers))
    }

    private fun updateTopThreeUsers() {
        // 초기화: 사용자 정보가 없는 경우 기본값 설정
        user1Name.text = ""
        user1Points.text = "0p"
        user2Name.text = ""
        user2Points.text = "0p"
        user3Name.text = ""
        user3Points.text = "0p"

        if (mRankerItems.size >= 1) {
            val user1 = mRankerItems[0]
            user1Image.setImageResource(user1.resourceId)
            user1Name.text = user1.name
            user1Points.text = "${user1.points}p"
        }
        if (mRankerItems.size >= 2) {
            val user2 = mRankerItems[1]
            user2Image.setImageResource(user2.resourceId)
            user2Name.text = user2.name
            user2Points.text = "${user2.points}p"
        }
        if (mRankerItems.size >= 3) {
            val user3 = mRankerItems[2]
            user3Image.setImageResource(user3.resourceId)
            user3Name.text = user3.name
            user3Points.text = "${user3.points}p"
        }
    }

    private fun updateMyPoints() {
        val myUserId = 1 // 사용자 ID를 1로 가정 (실제 앱에서는 로그인한 사용자 ID를 사용)
        val myPoints = databaseHelper.getPoints(myUserId)
        myPointsTextView.text = "${myPoints}p"
    }
}