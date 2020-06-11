package com.example.todolistproject.model

import androidx.room.Database
import androidx.room.RoomDatabase

// BASE DE DATOS QUE TIENE COMO ENTIDAD UserRoom una clase creada en UserRoom.kt de ejemplo

@Database(entities = [UserRoom::class, ListRoom::class, ItemRoom::class],version = 1)
abstract class Database: RoomDatabase(){
    abstract fun userRoomDao(): UserRoomDao
    abstract fun listRoomDao(): ListRoomDao
    abstract fun itemRoomDao(): ItemRoomDao
}

