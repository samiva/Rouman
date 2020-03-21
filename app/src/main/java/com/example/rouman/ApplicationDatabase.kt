package com.example.rouman

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rouman.ControlEvent
import com.example.rouman.ControlEventDao

@Database(entities = [ControlEvent::class], version = 1 )
abstract class AppDatabase : RoomDatabase() {
    abstract fun controlEventDao() : ControlEventDao
}


