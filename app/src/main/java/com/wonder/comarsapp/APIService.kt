package com.wonder.comarsapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getRemito(@Url url: String): Response<RemitoResponse>
}