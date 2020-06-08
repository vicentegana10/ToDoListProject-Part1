package com.example.todolistproject.networking

import com.example.todolistproject.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("users/get_self")
    fun getUser(@Header("token") token: String?): Call<User>
}