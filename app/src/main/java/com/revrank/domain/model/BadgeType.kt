package com.revrank.domain.model

/**
 * All 16 badges that a user can earn.
 */
enum class BadgeType(
    val id: String,
    val displayName: String,
    val description: String
) {
    FIRST_DRIVE("first_drive", "First Drive", "Complete your first trip"),
    CLUB_100KM("club_100km", "100km Club", "Drive 100km total"),
    VETERAN_1000KM("veteran_1000km", "1000km Veteran", "Drive 1000km total"),
    NIGHT_RIDER("night_rider", "Night Rider", "Complete 10 trips after 10 PM"),
    PERFECT_RUN("perfect_run", "Perfect Run", "Score 100 on a trip"),
    CONSISTENT("consistent", "Consistent", "Score 80+ on 5 consecutive trips"),
    THE_SURGEON("the_surgeon", "The Surgeon", "Braking score 100 three times"),
    SMOOTH_CRIMINAL("smooth_criminal", "Smooth Criminal", "Cornering score 100 three times"),
    ON_A_ROLL("on_a_roll", "On A Roll", "3-day driving streak"),
    WEEK_WARRIOR("week_warrior", "Week Warrior", "7-day driving streak"),
    IRON_DRIVER("iron_driver", "Iron Driver", "30-day driving streak"),
    FIRST_SHARE("first_share", "First Share", "Share a trip card"),
    COMEBACK_KID("comeback_kid", "Comeback Kid", "Improve score by 20+ vs previous trip"),
    HIGHWAY_STAR("highway_star", "Highway Star", "Reach 120 km/h"),
    SLOW_AND_STEADY("slow_and_steady", "Slow & Steady", "Avg speed under 40, score above 90"),
    WEEKEND_WARRIOR("weekend_warrior", "Weekend Warrior", "5 weekend trips");

    companion object {
        fun fromId(id: String): BadgeType? = BadgeType.entries.firstOrNull { it.id == id }
    }
}
