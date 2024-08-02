package com.example.healthyroutine

import android.annotation.SuppressLint
import android.app.TabActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TabHost
import com.example.healthyroutine.R
import com.example.healthyroutine.RoutineAddActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("deprecation")
class RecommendActivity : TabActivity() {

    //건강 루틴 추천 버튼
    lateinit var btnH1: Button
    lateinit var btnH2: Button
    lateinit var btnH3: Button
    lateinit var btnH4: Button
    lateinit var btnH5: Button
    lateinit var btnH6: Button
    lateinit var btnH7: Button
    lateinit var btnH8: Button

    //생활 루틴 추천 버튼
    lateinit var btnL1: Button
    lateinit var btnL2: Button
    lateinit var btnL3: Button
    lateinit var btnL4: Button
    lateinit var btnL5: Button
    lateinit var btnL6: Button
    lateinit var btnL7: Button
    lateinit var btnL8: Button

    lateinit var bottom_navigation: BottomNavigationView
    lateinit var bottom_navigation2: BottomNavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)
        setTitle("루틴 추천")

        val tabHost = tabHost

        val tabSpecHealth = tabHost.newTabSpec("HEALTH").setIndicator("건강 루틴")
        tabSpecHealth.setContent(R.id.tabHealth)
        tabHost.addTab(tabSpecHealth)

        val tabSpecLife = tabHost.newTabSpec("LIFE").setIndicator("생활 루틴")
        tabSpecLife.setContent(R.id.tabLife)
        tabHost.addTab(tabSpecLife)

        tabHost.currentTab = 0

        btnH1 = findViewById<Button>(R.id.btnH1)
        btnH1.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnH1.text.toString())
            startActivity(intent)
        }

        btnH2 = findViewById<Button>(R.id.btnH2)
        btnH2.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnH2.text.toString())
            startActivity(intent)
        }

        btnH3 = findViewById<Button>(R.id.btnH3)
        btnH3.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnH3.text.toString())
            startActivity(intent)
        }

        btnH4 = findViewById<Button>(R.id.btnH4)
        btnH4.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnH4.text.toString())
            startActivity(intent)
        }

        btnH5 = findViewById<Button>(R.id.btnH5)
        btnH5.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnH5.text.toString())
            startActivity(intent)
        }

        btnH6 = findViewById<Button>(R.id.btnH6)
        btnH6.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnH6.text.toString())
            startActivity(intent)
        }

        btnH7 = findViewById<Button>(R.id.btnH7)
        btnH7.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnH7.text.toString())
            startActivity(intent)
        }

        btnH8 = findViewById<Button>(R.id.btnH8)
        btnH8.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnH8.text.toString())
            startActivity(intent)
        }

        btnL1 = findViewById<Button>(R.id.btnL1)
        btnL1.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnL1.text.toString())
            startActivity(intent)
        }

        btnL2 = findViewById<Button>(R.id.btnL2)
        btnL2.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnL2.text.toString())
            startActivity(intent)
        }

        btnL3 = findViewById<Button>(R.id.btnL3)
        btnL3.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnL3.text.toString())
            startActivity(intent)
        }

        btnL4 = findViewById<Button>(R.id.btnL4)
        btnL4.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnL4.text.toString())
            startActivity(intent)
        }

        btnL5 = findViewById<Button>(R.id.btnL5)
        btnL5.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnL5.text.toString())
            startActivity(intent)
        }

        btnL6 = findViewById<Button>(R.id.btnL6)
        btnL6.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnL6.text.toString())
            startActivity(intent)
        }

        btnL7 = findViewById<Button>(R.id.btnL7)
        btnL7.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnL7.text.toString())
            startActivity(intent)
        }

        btnL8 = findViewById<Button>(R.id.btnL8)
        btnL8.setOnClickListener {
            var intent = Intent(this, RoutineAddActivity::class.java)
            intent.putExtra("BUTTON_TEXT", btnL8.text.toString())
            startActivity(intent)
        }

        // 건강 루틴 탭 - BottomNavigationView 설정
        bottom_navigation = findViewById(R.id.bottom_navigation)
        bottom_navigation.selectedItemId = R.id.navigation_recommend

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

        // 생활루틴 탭 - BottomNavigationView 설정
        bottom_navigation2 = findViewById(R.id.bottom_navigation2)
        bottom_navigation2.selectedItemId = R.id.navigation_recommend

        bottom_navigation2.setOnNavigationItemSelectedListener { item ->
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
}
