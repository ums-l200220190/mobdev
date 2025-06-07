package com.example.donordarah

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendonorDao {
    @Insert
    fun insert(pendonor: Pendonor)

    @Query("SELECT * FROM Pendonor")
    fun getAll(): List<Pendonor>
}
