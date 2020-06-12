package com.example.todolistproject.networking

import com.example.todolistproject.model.ItemRoom
import retrofit2.Call
import retrofit2.http.*

interface ItemApi {
    @POST("items")
    fun postListApi(@Header("token") token: String?,
                 @Body()itemsRoom: List<ItemRoom>
    ): Call<ItemRoom>

    @PUT("items/:{item_id}")
    fun updateItemApi(@Header("token") token: String?,
                      @Path("item_id") item_id: Int?,
                      @Body()itemRoom: ItemRoom
    ): Call<ItemRoom>

    @GET("items/:{item_id}")
    fun getItemApi(@Header("token") token: String?,
                   @Path("item_id") item_id: Int?
    ): Call<ItemRoom>

    @GET("items/?list_id={list_id}")
    fun getItemsApi(@Header("token") token: String?,
                   @Path("list_id") list_id: Int?
    ): Call<ItemRoom>

    @DELETE("items/:{item_id}")
    fun deleteItemApi(@Header("token") token: String?,
                      @Path("item_id") list_id: Int?,
                      @Body()itemRoom: ItemRoom
    ): Call<ItemRoom>
}