package com.example.tracking_app.network


import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "http://192.168.43.64:9092"
// private const val BASE_URL = "http://172.16.216.155:9091"
//private const val BASE_URL = "http://192.168.56.1:9091"
//private const val BASE_URL = "http://192.168.43.64:9090"

private val service: ApiService by lazy {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // TODO: Activate connectivityInterceptor and remove
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    retrofit.create(ApiService::class.java)
}

// todo: See how to optimize this call
private val serviceWithBearer: ApiService by lazy {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(BearerTokenInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    retrofit.create(ApiService::class.java)
}

fun getNetworkService() = service

fun getNetworkServiceWithBearer() = serviceWithBearer


