package com.terrranullius.pickcab.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerResponse(
     @Json(name = "result") val result: String
)
