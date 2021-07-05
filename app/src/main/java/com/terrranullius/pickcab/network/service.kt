package com.terrranullius.pickcab.network

import androidx.lifecycle.LiveData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.terrranullius.pickcab.network.GenericApiResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private const val BASE_URL = "https://pickcab.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addCallAdapterFactory(LiveDataCallAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface PickCabApiService {

    @POST("SendConfirmation")
    fun sendConfirmation(
        @Body
        request: ConfirmationRequest
    ) : LiveData<GenericApiResponse<ServerResponse>>

    @POST("verify/number/otp")
    @FormUrlEncoded
    fun verifyOtp(
        @Field("number") number: Long,
        @Field("otp") otp: Int
    ) : LiveData<GenericApiResponse<ServerResponse>>


    @POST("verify/number")
    @FormUrlEncoded
     fun startVerification(
        @Field("number") number: Long
    ) : LiveData<GenericApiResponse<ServerResponse>>
}

object PickCabApi {
    val retrofitService: PickCabApiService by lazy { retrofit.create(PickCabApiService::class.java) }
}