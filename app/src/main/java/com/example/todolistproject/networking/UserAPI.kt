package com.example.todolistproject.networking

import com.example.todolistproject.User
import retrofit2.Call
import retrofit2.http.GET

interface UserApi {
    @GET("get_user_info")
    fun getUser(): Call<User>
}