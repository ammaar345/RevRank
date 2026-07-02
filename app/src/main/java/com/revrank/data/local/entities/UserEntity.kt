package com.revrank.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val username: String,
    val displayName: String,
    val rank: Int = 1,
    val xp: Int = 0,
    val totalTrips: Int = 0,
    val totalDistanceKm: Float = 0f,
    val streakDays: Int = 0,
    val lastTripDate: Long?,
    val isPro: Boolean = false
)
