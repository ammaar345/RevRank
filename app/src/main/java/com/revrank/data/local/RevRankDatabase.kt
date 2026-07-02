package com.revrank.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.revrank.data.local.dao.BadgeDao
import com.revrank.data.local.dao.TripDao
import com.revrank.data.local.dao.UserDao
import com.revrank.data.local.entities.BadgeEntity
import com.revrank.data.local.entities.TripEntity
import com.revrank.data.local.entities.UserEntity

@Database(
    entities = [TripEntity::class, UserEntity::class, BadgeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RevRankDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun userDao(): UserDao
    abstract fun badgeDao(): BadgeDao
}
