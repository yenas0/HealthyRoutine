package com.example.healthyroutine

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.ArrayList

class RankingActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: RankingAdapter
    private lateinit var mRankerItems: ArrayList<RankingUserActivity>
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 파이어베이스 데이터베이스 초기화
        database = FirebaseDatabase.getInstance().reference

        mRecyclerView = findViewById(R.id.ranking_recyclerView)

        // initiate adapter
        mRecyclerAdapter = RankingAdapter()

        // initiate recyclerview
        mRecyclerView.adapter = mRecyclerAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)

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
                for (i in mRankerItems.indices) {
                    mRankerItems[i].ranking = i + 1
                }
                mRecyclerAdapter.setUserList(mRankerItems)
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리
                // 로그 출력 또는 사용자에게 알림
                println("Failed to read value: ${error.toException()}")
            }
        })
    }
}