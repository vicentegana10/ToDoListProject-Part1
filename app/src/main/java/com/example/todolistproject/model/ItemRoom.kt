package com.example.todolistproject.model

import android.os.Parcelable
import androidx.room.*
import com.example.todolistproject.model.ItemRoom.Companion.DONE
import com.example.todolistproject.model.ItemRoom.Companion.ID
import com.example.todolistproject.model.ItemRoom.Companion.LIST_ID
import com.example.todolistproject.model.ItemRoom.Companion.POSITION
import com.example.todolistproject.model.ItemRoom.Companion.TABLE_NAME
import kotlinx.android.parcel.Parcelize

@Dao
interface ItemRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: ItemRoom)
    @Query("UPDATE ${TABLE_NAME} SET ${ID} = :newId WHERE ${ID} = :oldId")
    fun updateIdItem(newId: Int?, oldId: Int?)
    @Delete
    fun deleteItem(item: ItemRoom)
    @Query("SELECT * FROM ${TABLE_NAME} WHERE ${ID} = :id")
    fun getItem(id:Int): ItemRoom
    @Query("SELECT * FROM ${TABLE_NAME} ORDER BY ${ID} DESC LIMIT 1")
    fun getLastItem(): ItemRoom
    //Busca todos los items completados o no completados
    @Query("SELECT * FROM ${TABLE_NAME} WHERE ${LIST_ID} = :list_id AND ${DONE} = :done ORDER BY ${POSITION} ASC")
    fun getItems(list_id:Int, done:Boolean): List<ItemRoom>
    @Query("SELECT * FROM ${TABLE_NAME} WHERE ${LIST_ID} = :list_id")
    fun getItemsList(list_id:Int): List<ItemRoom>
    @Query("SELECT * FROM ${TABLE_NAME}")
    //Retorna todos los Items
    fun getAllItems(): List<ItemRoom>
    //Retorna toda la lista de Items ordenados segun su posicion
    @Query("SELECT * FROM ${TABLE_NAME} WHERE ${LIST_ID} = :list_id ORDER BY ${POSITION} ASC")
    fun getAllItemsOrdered(list_id: Int): List<ItemRoom>
}

@Parcelize
@Entity(tableName = TABLE_NAME)
data class ItemRoom(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int?,
    @ColumnInfo(name = NAME)
    var name: String,
    @ColumnInfo(name = POSITION)
    var position: Int,
    @ColumnInfo(name = LIST_ID)
    var list_id: Int,
    @ColumnInfo(name = STARRED)
    var starred: Boolean,
    @ColumnInfo(name = DONE)
    var done: Boolean,
    @ColumnInfo(name = DUE_DATE)
    var due_date: String,
    @ColumnInfo(name = NOTES)
    var notes: String,
    @ColumnInfo(name = LAT)
    var lat: Double,
    @ColumnInfo(name = LONG)
    var longi: Double


): Parcelable {
    companion object {
        const val TABLE_NAME = "item"
        const val ID = "id"
        const val NAME = "name"
        const val POSITION = "position"
        const val LIST_ID = "list_id"
        const val STARRED = "starred"
        const val DONE = "done"
        const val DUE_DATE = "due_date"
        const val NOTES = "notes"
        const val LAT = "latitude"
        const val LONG = "long"

    }
}
//Clase auxiliar que envia a la api los Items
data class ApiItem(var items: List<ItemApi>)

data class ItemApi(var id: Int?,
                var name: String,
                var position: Int,
                var list_id: Int,
                var starred: Boolean,
                var done: Boolean,
                var due_date: String,
                var notes: String,
                var lat: Double,
                var long: Double)