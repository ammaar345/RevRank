package com.revrank.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.revrank.data.local.entities.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges WHERE userId = :userId ORDER BY earnedAt DESC")
    fun getBadgesForUser(userId: String): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Query("SELECT COUNT(*) FROM badges WHERE userId = :userId")
    suspend fun getBadgeCount(userId: String): Int
}
