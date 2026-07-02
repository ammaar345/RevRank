package com.revrank.domain.model

data class Badge(
    val id: String,
    val userId: String,
    val earnedAt: Long,
    val badgeType: String
)
