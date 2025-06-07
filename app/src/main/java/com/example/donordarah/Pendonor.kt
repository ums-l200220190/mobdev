package com.example.donordarah

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pendonor (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val umur: Int,
    val berat: Int,
    val golongan: String,
    val riwayat: Boolean,
    val status: String,
    val waktu: Long
)