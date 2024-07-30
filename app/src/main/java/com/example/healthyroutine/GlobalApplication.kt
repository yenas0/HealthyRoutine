package com.example.healthyroutine

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kakao Sdk 초기화
        KakaoSdk.init(this, "75b8c085854198b90d474828189e42c8")
    }
}
