package com.revrank.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.List

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val startTime: Long,
    val endTime: Long?,
    val distanceKm: Float,
    val maxSpeedKmh: Float,
    val avgSpeedKmh: Float,
    val score: Int,
    val scoreAcceleration: Int,
    val scoreBraking: Int,
    val scoreCornering: Int,
    val scoreSmoothness: Int,
    val scoreConsistency: Int,
    val routeName: String?,
    val shareImagePath: String?,
    val isSynced: Boolean = false,
    val wasShared: Boolean = false,
    // Pro features: route replay data (GPS points)
    val gpsPointsJson: String? = null,
    // Pro features: G-force visualizer data
    val gForcePointsJson: String? = null
) {
    // Type converters for JSON<List<...>> storage
    companion object {
        private val gson = Gson()
        private val pointListType: Type = object : TypeToken<List<GpsPoint>>() {}.type
        private val gForcePointListType: Type = object : TypeToken<List<GForcePoint>>() {}.type

        @TypeConverter
        fun gpsPointsToJson(points: List<GpsPoint>?): String? =
            points?.let { gson.toJson(it) }

        @TypeConverter
        fun jsonToGpsPoints(json: String?): List<GpsPoint>? =
            json?.let { gson.fromJson(it, pointListType) } ?: emptyList()

        @TypeConverter
        fun gForcePointsToJson(points: List<GForcePoint>?): String? =
            points?.let { gson.toJson(it) }

        @TypeConverter
        fun jsonToGForcePoints(json: String?): List<GForcePoint>? =
            json?.let { gson.fromJson(it, gForcePointListType) } ?: emptyList()
    }

    // Convenience getters (non-persisted)
    val gpsPoints: List<GpsPoint>
        get() = jsonToGpsPoints(gpsPointsJson)

    val gForcePoints: List<GForcePoint>
        get() = jsonToGForcePoints(gForcePointsJson)
}

// Simple data classes for storage
data class GpsPoint(
    val lat: Double,
    val lng: Double,
    val timestamp: Long,
    val speedKmh: Float?,
    val accuracy: Float?
)

data class GForcePoint(
    val timestamp: Long,
    val lateralG: Float,
    val longitudinalG: Float,
    val scoreAtMoment: Int?
)