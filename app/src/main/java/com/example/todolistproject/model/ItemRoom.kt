package com.example.todolistproject.model

import android.os.Parcelable
import androidx.room.*
import com.example.todolistproject.model.ItemRoom.Companion.ID
import com.example.todolistproject.model.ItemRoom.Companion.POSITION
import com.example.todolistproject.model.ItemRoom.Companion.TABLE_NAME
import kotlinx.android.parcel.Parcelize

@Dao
interface ItemRoomDao {

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
    var notes: String

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

    }
}