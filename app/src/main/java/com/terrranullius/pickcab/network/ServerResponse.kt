package com.terrranullius.pickcab.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class ServerResponse(
     @Json(name = "result") val result: String
)
