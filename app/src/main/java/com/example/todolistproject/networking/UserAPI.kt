package com.example.todolistproject.networking
import androidx.room.Update
import com.example.todolistproject.model.UserRoom
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    @GET("users/get_self")
    fun getUser(@Header("token") token: String?): Call<UserRoom>

    @PUT("users/update_self")
    fun updateUser(@Header("token") token: String?,
                   @Body()userRoom: UserRoom): Call<UserRoom>
}