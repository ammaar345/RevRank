package com.revrank.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.revrank.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserById(uid: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET username = :username WHERE uid = :uid")
    suspend fun updateUsername(uid: String, username: String)

    @Query("UPDATE users SET isPro = :isPro WHERE uid = :uid")
    suspend fun updateProStatus(uid: String, isPro: Boolean)
}
