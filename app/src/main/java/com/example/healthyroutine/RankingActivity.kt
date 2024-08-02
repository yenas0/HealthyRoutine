package com.example.healthyroutine

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class RankingActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: RankingAdapter
    private lateinit var mRankerItems: ArrayList<RankingUserActivity>
    private lateinit var database: DatabaseReference

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        // 파이어베이스 데이터베이스 초기화
        database = FirebaseDatabase.getInstance().reference

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

        // 파이어베이스에서 데이터 가져오기
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        mRankerItems = ArrayList()
        database.child("rankings").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mRankerItems.clear()
                for (dataSnapshot in snapshot.children) {
                    val name = dataSnapshot.child("name").getValue(String::class.java) ?: ""
                    val points = dataSnapshot.child("points").getValue(Int::class.java) ?: 0
                    val profilePic = R.drawable.ic_profile
                    mRankerItems.add(RankingUserActivity(0, profilePic, name, points)) // 초기 랭킹 0으로 설정
                }
                // 포인트 순으로 정렬
                mRankerItems.sortByDescending { it.points }
                // 랭킹 할당
                /*for (i in mRankerItems.indices) {
                    mRankerItems[i].ranking = i + 1
                }*/
                //mRecyclerAdapter.setUserList(mRankerItems)

                // 상위 3명 사용자 정보 업데이트
                updateTopThreeUsers()

                // 4위 부터 사용자 정보
                val remainingUsers = mRankerItems.drop(3)
                mRecyclerAdapter.setUserList(ArrayList(remainingUsers))

            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리
                // 로그 출력 또는 사용자에게 알림
                println("Failed to read value: ${error.toException()}")
            }
        })
    }

    private fun updateTopThreeUsers() {
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
}