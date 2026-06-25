package com.example.kalku.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"])]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val businessName: String,
    val email: String,
    val password: String,
    @ColumnInfo(defaultValue = "''")
    val phone: String = "",
    @ColumnInfo(defaultValue = "''")
    val address: String = "",
    @ColumnInfo(defaultValue = "''")
    val businessDescription: String = "",
    @ColumnInfo(defaultValue = "''")
    val photoUri: String = ""
)
