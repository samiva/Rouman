package com.example.rouman

import androidx.room.*

@Entity(tableName = "control_events")
data class ControlEvent(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name="time") var time: Long?,
    @ColumnInfo(name="relay") var relay: String?,
    @ColumnInfo(name="setting") var setting: String,
    @ColumnInfo(name="number") var number: String
)

@Dao
interface ControlEventDao {
    @Transaction @Insert
    fun insert(controlEvent: ControlEvent)

    @Query("SELECT * FROM control_events s ORDER BY s.time DESC")
    fun getControlEvents(): List<ControlEvent>

    @Query("DELETE FROM control_events WHERE time = :time AND relay = :relay AND setting = :setting AND number = :number")
    fun deleteRowByData(time: Long?, relay: String?, setting: String? , number:String?)

    @Query("DELETE FROM control_events WHERE time > 0")
    fun clearDb()

    @Transaction  @Delete
    fun delete(model: ControlEvent?)
}