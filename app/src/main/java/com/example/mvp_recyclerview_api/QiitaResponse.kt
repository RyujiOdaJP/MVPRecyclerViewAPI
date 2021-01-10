package com.example.mvp_recyclerview_api

data class QiitaResponse(
        val url: String?,
        val title: String?,
        val user: User?
)

//階層が分かれている時は分ける
data class User(
        val id: String?
)