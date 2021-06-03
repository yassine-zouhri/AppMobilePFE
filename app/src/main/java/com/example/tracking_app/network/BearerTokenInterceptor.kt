package com.example.tracking_app.network

import com.example.tracking_app.utils.SessionManagerUtil
import okhttp3.Interceptor
import okhttp3.Response

class BearerTokenInterceptor : Interceptor {

    private var accessToken: String = SessionManagerUtil.getSessionAccessToken() ?: ""

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request
            .newBuilder()
            .header("Authorization", accessToken)
            .build()
        return chain.proceed(request)
    }
}