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

    @PUT("lists/{list_id}")
    fun updateListApi(@Header("token") token: String?,
                   @Path("list_id") list_id: Int?,
                   @Body()listRoom: ListRoom
    ): Call<ListRoom>

    @GET("lists/")
    fun getListsApi(@Header("token") token: String?): Call<ListRoom>

    @GET("lists/{list_id}")
    fun getListApi(@Header("token") token: String?,
                @Path("list_id") list_id: Int?
    ): Call<ListRoom>

    @DELETE("lists/{list_id}")
    fun deleteListApi(@Header("token") token: String?,
                @Path("list_id") list_id: Int?
    ): Call<ListRoom>



}