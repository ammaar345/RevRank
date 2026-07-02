package com.revrank.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val earnedAt: Long,
    val badgeType: String
)
