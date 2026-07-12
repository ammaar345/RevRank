package com.revrank.domain.usecase

import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.Trip
import com.revrank.domain.model.User
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Evaluates which badges a user earns after a trip.
 * Only returns badges the user did NOT already have.
 */
@Singleton
class BadgeEvaluator @Inject constructor() {

    fun evaluate(trip: Trip, allTrips: List<Trip>, existingBadges: List<BadgeType>): List<BadgeType> {
        val newBadges = mutableListOf<BadgeType>()
        val currentIds = existingBadges.map { it.id }.toSet()

        // FIRST_DRIVE
        if (BadgeType.FIRST_DRIVE.id !in currentIds && allTrips.count { it.endTime != null } >= 1) {
            newBadges += BadgeType.FIRST_DRIVE
        }

        // CLUB_100KM
        if (BadgeType.CLUB_100KM.id !in currentIds) {
            val totalKm = allTrips.filter { it.endTime != null }.sumOf { it.distanceKm.toDouble() }
            if (totalKm >= 100) newBadges += BadgeType.CLUB_100KM
        }

        // VETERAN_1000KM
        if (BadgeType.VETERAN_1000KM.id !in currentIds) {
            val totalKm = allTrips.filter { it.endTime != null }.sumOf { it.distanceKm.toDouble() }
            if (totalKm >= 1000) newBadges += BadgeType.VETERAN_1000KM
        }

        // NIGHT_RIDER
        if (BadgeType.NIGHT_RIDER.id !in currentIds) {
            val nightTrips = allTrips.filter { it.endTime != null && isAfter10Pm(it.endTime!!) }
            if (nightTrips.size >= 10) newBadges += BadgeType.NIGHT_RIDER
        }

        // PERFECT_RUN
        if (BadgeType.PERFECT_RUN.id !in currentIds && trip.score == 100) {
            newBadges += BadgeType.PERFECT_RUN
        }

        // CONSISTENT
        if (BadgeType.CONSISTENT.id !in currentIds) {
            val endedTrips = allTrips.filter { it.endTime != null }.sortedBy { it.endTime }
            val lastFive = endedTrips.takeLast(5)
            if (lastFive.size == 5 && lastFive.all { it.score >= 80 }) {
                newBadges += BadgeType.CONSISTENT
            }
        }

        // THE_SURGEON
        if (BadgeType.THE_SURGEON.id !in currentIds) {
            val highBrakingTrips = allTrips.filter { it.endTime != null && it.scoreBraking == 100 }
            if (highBrakingTrips.size >= 3) newBadges += BadgeType.THE_SURGEON
        }

        // SMOOTH_CRIMINAL
        if (BadgeType.SMOOTH_CRIMINAL.id !in currentIds) {
            val highCorneringTrips = allTrips.filter { it.endTime != null && it.scoreCornering == 100 }
            if (highCorneringTrips.size >= 3) newBadges += BadgeType.SMOOTH_CRIMINAL
        }

        // ON_A_ROLL
        if (BadgeType.ON_A_ROLL.id !in currentIds && drivingStreak(allTrips) >= 3) {
            newBadges += BadgeType.ON_A_ROLL
        }

        // WEEK_WARRIOR
        if (BadgeType.WEEK_WARRIOR.id !in currentIds && drivingStreak(allTrips) >= 7) {
            newBadges += BadgeType.WEEK_WARRIOR
        }

        // IRON_DRIVER
        if (BadgeType.IRON_DRIVER.id !in currentIds && drivingStreak(allTrips) >= 30) {
            newBadges += BadgeType.IRON_DRIVER
        }

        // FIRST_SHARE - handled externally (triggered when share button is tapped)
        if (BadgeType.FIRST_SHARE.id !in currentIds && trip.wasShared) {
            newBadges += BadgeType.FIRST_SHARE
        }

        // COMEBACK_KID
        if (BadgeType.COMEBACK_KID.id !in currentIds) {
            val sortedTrips = allTrips.filter { it.endTime != null }.sortedBy { it.endTime }
            if (sortedTrips.size >= 2) {
                val prev = sortedTrips[sortedTrips.size - 2]
                if (trip.score - prev.score >= 20) {
                    newBadges += BadgeType.COMEBACK_KID
                }
            }
        }

        // HIGHWAY_STAR
        if (BadgeType.HIGHWAY_STAR.id !in currentIds && trip.maxSpeedKmh >= 120) {
            newBadges += BadgeType.HIGHWAY_STAR
        }

        // SLOW_AND_STEADY
        if (BadgeType.SLOW_AND_STEADY.id !in currentIds && trip.avgSpeedKmh < 40 && trip.score > 90) {
            newBadges += BadgeType.SLOW_AND_STEADY
        }

        // WEEKEND_WARRIOR
        if (BadgeType.WEEKEND_WARRIOR.id !in currentIds) {
            val weekendTrips = allTrips.filter { it.endTime != null && isWeekend(it.endTime!!) }
            if (weekendTrips.size >= 5) newBadges += BadgeType.WEEKEND_WARRIOR
        }

        return newBadges
    }

    private fun isAfter10Pm(timestamp: Long): Boolean {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        return hour >= 22
    }

    private fun isWeekend(timestamp: Long): Boolean {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        val day = cal.get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    fun drivingStreak(allTrips: List<Trip>): Int {
        if (allTrips.isEmpty()) return 0
        val sortedTrips = allTrips.filter { it.endTime != null }.sortedBy { it.endTime }
        var streak = 1
        var maxStreak = 1
        for (i in 1 until sortedTrips.size) {
            val prevDate = java.util.Date(allTrips[sortedTrips.size - i].endTime!!)
            val currDate = java.util.Date(allTrips[sortedTrips.size - (i - 1)].endTime!!)
            val prevCal = Calendar.getInstance().apply { time = prevDate }
            val currCal = Calendar.getInstance().apply { time = currDate }
            // Check if currCal is the next day of prevCal
            prevCal.add(Calendar.DAY_OF_YEAR, 1)
            if (prevCal.get(Calendar.YEAR) == currCal.get(Calendar.YEAR) &&
                prevCal.get(Calendar.DAY_OF_YEAR) == currCal.get(Calendar.DAY_OF_YEAR)) {
                streak++
                maxStreak = maxOf(maxStreak, streak)
            } else {
                streak = 1
            }
        }
        return maxStreak
    }
}
