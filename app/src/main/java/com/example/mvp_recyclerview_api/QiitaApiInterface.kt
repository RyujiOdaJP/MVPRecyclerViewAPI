package com.example.mvp_recyclerview_api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QiitaApiInterface {
    @GET("https://qiita.com/api/v2/items")
    fun apiDemo(
            @Query("page") page: Int,
            @Query("par_page") perPage: Int
    ): Call<List<QiitaResponse>>
}