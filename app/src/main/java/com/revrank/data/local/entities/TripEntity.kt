package com.revrank.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revrank.domain.model.GForcePoint
import com.revrank.domain.model.GpsPoint
import java.lang.reflect.Type

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
    // JSON <-> List converters for Room storage. Uses the domain point models
    // directly so no entity<->domain mapping is needed for point data.
    companion object {
        private val gson = Gson()
        private val pointListType: Type = object : TypeToken<List<GpsPoint>>() {}.type
        private val gForcePointListType: Type = object : TypeToken<List<GForcePoint>>() {}.type

        @JvmStatic
        @TypeConverter
        fun gpsPointsToJson(points: List<GpsPoint>?): String? =
            points?.let { gson.toJson(it) }

        @JvmStatic
        @TypeConverter
        fun jsonToGpsPoints(json: String?): List<GpsPoint> =
            json?.let { gson.fromJson<List<GpsPoint>>(it, pointListType) } ?: emptyList()

        @JvmStatic
        @TypeConverter
        fun gForcePointsToJson(points: List<GForcePoint>?): String? =
            points?.let { gson.toJson(it) }

        @JvmStatic
        @TypeConverter
        fun jsonToGForcePoints(json: String?): List<GForcePoint> =
            json?.let { gson.fromJson<List<GForcePoint>>(it, gForcePointListType) } ?: emptyList()
    }

    // Convenience getters (non-persisted)
    val gpsPoints: List<GpsPoint>
        get() = jsonToGpsPoints(gpsPointsJson)

    val gForcePoints: List<GForcePoint>
        get() = jsonToGForcePoints(gForcePointsJson)
}
