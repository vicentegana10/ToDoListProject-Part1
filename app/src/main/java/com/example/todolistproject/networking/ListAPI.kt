package com.example.todolistproject.networking

import com.example.todolistproject.model.ListRoom
import com.example.todolistproject.model.UserRoom
import retrofit2.Call
import retrofit2.http.*

interface ListApi {
    @POST("lists/")
    fun postList(@Header("token") token: String?,
                @Body()listRoom: ListRoom
    ): Call<ListRoom>

    @PUT("users/update_self")
    fun updateUser(@Header("token") token: String?,
                   @Body()userRoom: UserRoom
    ): Call<ListRoom>
}