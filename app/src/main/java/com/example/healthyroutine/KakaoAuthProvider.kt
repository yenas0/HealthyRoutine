package com.example.healthyroutine

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.OAuthProvider

object KakaoAuthProvider {
    fun getCredential(accessToken: String): AuthCredential {
        return OAuthProvider.getCredential("kakao.com", accessToken, "")
    }
}
