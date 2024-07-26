package com.example.healthyroutine.com.example.healthyroutine

import android.app.TabActivity
import android.os.Bundle
import android.widget.TabHost
import com.example.healthyroutine.R

@Suppress("deprecation")
class RecommendActivity : TabActivity() {

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
    }
}
