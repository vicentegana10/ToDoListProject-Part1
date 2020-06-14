package com.example.todolistproject.model

import com.example.todolistproject.model.ListRoom.Companion.TABLE_NAME


import android.os.Parcelable
import androidx.room.*
import com.example.todolistproject.model.ListRoom.Companion.ID
import com.example.todolistproject.model.ListRoom.Companion.POSITION
import kotlinx.android.parcel.Parcelize
import kotlin.collections.List

@Dao
interface ListRoomDao {
    // ESTA INSERTA UN USERROOM A LA BBDD QUE SE PASA COMO PARAMETRO
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: ListRoom)
    // ESTA ACTUALIZA UN USERROOM DE LA BBDD QUE SE PASA COMO PARAMETRO
    /*@Update
    fun updateList(list: ListRoom)*/
    @Query("UPDATE ${TABLE_NAME} SET ${ID} = :newId WHERE ${ID} = :oldId")
    fun updateIdList(newId: Int?, oldId: Int?)
    @Delete
    fun deleteList(list: ListRoom)
    @Query("SELECT * FROM ${TABLE_NAME} WHERE ${ID} = :id")
    fun getList(id:Int): ListRoom
    @Query("SELECT * FROM ${TABLE_NAME} ORDER BY ${ID} DESC LIMIT 1")
    fun getLastList(): ListRoom
    @Query("SELECT * FROM ${TABLE_NAME}")
    fun getAllList(): List<ListRoom>
    @Query("SELECT * FROM ${TABLE_NAME} ORDER BY ${POSITION} ASC")
    fun getAllListOrdered(): List<ListRoom>
}

@Parcelize
@Entity(tableName = TABLE_NAME)
data class ListRoom(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @ColumnInfo(name = NAME)
    var name: String,
    @ColumnInfo(name = POSITION)
    var position: Int
): Parcelable {
    companion object {
        const val TABLE_NAME = "list"
        const val ID = "id"
        const val NAME = "name"
        const val POSITION = "position"
    }
}
