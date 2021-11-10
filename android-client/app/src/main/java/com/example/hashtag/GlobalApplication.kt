package com.example.hashtag

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "145e4cd4a3c29e4ad6692c4159193440")
    }
}