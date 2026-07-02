package com.revrank.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.revrank.data.local.entities.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY startTime DESC")
    fun getTripsForUser(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY startTime DESC LIMIT 10")
    fun getRecentTripsForUser(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTrip(tripId: String)

    @Query("SELECT * FROM trips WHERE isSynced = 0")
    suspend fun getUnsyncedTrips(): List<TripEntity>
}
