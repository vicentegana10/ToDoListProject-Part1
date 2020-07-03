package com.example.todolistproject.networking

import android.graphics.Shader
import com.example.todolistproject.model.ListRoom
import com.example.todolistproject.model.ShareList
import com.example.todolistproject.model.SharedListId
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SharedListsAPI {
    @GET("shared_lists/")
    fun getSharedLists(@Header("token") token: String?): Call<List<SharedListId>>

    @POST("shared_lists/share")
    fun postSharedList(@Header("token") token: String?,
                       @Body()shareList: ShareList): Call<ShareList>
}