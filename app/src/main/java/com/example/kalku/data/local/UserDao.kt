package com.example.kalku.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) AND id != :userId LIMIT 1")
    suspend fun getOtherUserByEmail(email: String, userId: Int): UserEntity?

    @Query("UPDATE users SET password = :newPassword WHERE LOWER(email) = LOWER(:email)")
    suspend fun updatePassword(email: String, newPassword: String): Int
}
