package com.example.todolistproject.networking

import com.example.todolistproject.model.ListRoom
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SharedListsAPI {
    @GET("shared_lists/")
    fun getSharedLists(@Header("token") token: String?): Call<List<ListRoom>>

    @POST("shared_lists/share")
    fun postSharedList(@Header("token") token: String?,
                       @Body()listRoom: ListRoom): Call<ListRoom>
}