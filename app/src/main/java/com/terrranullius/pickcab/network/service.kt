package com.terrranullius.pickcab.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.terrranullius.pickcab.other.Constants.AUTH_FAST2SMS
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL_SMS = "https://pickcab.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_SMS)
    .build()

interface SmsApiService {

    @POST("SendConfirmation")
    suspend fun sendConfirmation(
       @Body
       request: ConfirmationRequest
    )
}

object SmsApi {
    val retrofitService: SmsApiService by lazy { retrofit.create(SmsApiService::class.java) }
}