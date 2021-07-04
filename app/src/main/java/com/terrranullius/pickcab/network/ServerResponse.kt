package com.terrranullius.pickcab.network

import com.squareup.moshi.Json

data class ServerResponse(
     @Json(name = "result") val result: String
)
