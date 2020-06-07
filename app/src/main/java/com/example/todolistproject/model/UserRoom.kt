package com.example.todolistproject.model

import androidx.room.*
import com.example.todolistproject.model.UserRoom.Companion.EMAIL
import com.example.todolistproject.model.UserRoom.Companion.TABLE_NAME

// ACA VAN LAS CONSULTAS A LA BASE DE DATOS

@Dao
interface UserRoomDao {
    // ESTA INSERTA UN USERROOM A LA BBDD QUE SE PASA COMO PARAMETRO
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg userToInsert: UserRoom)
    // ESTA ACTUALIZA UN USERROOM DE LA BBDD QUE SE PASA COMO PARAMETRO
    @Update
    fun updateUser(userToUpdate: UserRoom)
    // ESTA RECIBE TODOS LOS  USERROOM DE LA BBDD Y LOS PONE EN UNA LISTA
    @Query("SELECT * FROM ${TABLE_NAME}")
    fun getAllUsers(): List<UserRoom>
    // ESTA RECIBE UN USERROOM DE LA BBDD SEGUN EL MAIL ENTREGADO COMO PARAMETRO
    @Query("SELECT * FROM ${TABLE_NAME} WHERE ${EMAIL}=:mail")
    fun getUserRoomData(mail: String): UserRoom
}

// ESTA ES LA CLASE USERROOM COMO ENTIDAD PARA LA BBDD, HABRIA QUE PONER USER NOMAS
@Entity(tableName = TABLE_NAME)
data class UserRoom(
    @PrimaryKey
    @ColumnInfo(name = EMAIL)
    val email: String,
    @ColumnInfo(name = NAME)
    val name: String,
    @ColumnInfo(name = LAST_NAME)
    val last_name: String,
    @ColumnInfo(name = PHONE)
    val phone: String,
    @ColumnInfo(name = PROFILE_PHOTO)
    val profile_photo: String,
    @ColumnInfo(name = PASSWORD)
    val password: String   /*,
    @ColumnInfo(name = TO_DO_LISTS)
    val to_do_lists:  ArrayList<com.example.todolistproject.List>
    */

) {
    companion object {
        const val TABLE_NAME = "userRoom"
        const val EMAIL = "email"
        const val NAME = "name"
        const val LAST_NAME = "last_name"
        const val PHONE = "phone"
        const val PROFILE_PHOTO = "profile_photo"
        const val PASSWORD = "password"
        const val TO_DO_LISTS = "to_do_lists"

    }
}