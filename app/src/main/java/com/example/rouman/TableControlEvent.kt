package com.example.rouman

import androidx.room.*

@Entity(tableName = "control_events")
data class ControlEvent(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name="time") var time: Long?,
    @ColumnInfo(name="relay") var relay: String?,
    @ColumnInfo(name="setting") var setting: String
)

@Dao
interface ControlEventDao {
    @Transaction @Insert
    fun insert(controlEvent: ControlEvent)

    @Query("SELECT * FROM control_events")
    fun getControlEvents(): List<ControlEvent>
}