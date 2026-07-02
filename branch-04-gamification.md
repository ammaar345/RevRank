# Branch 04 — Gamification (Ranks, Streaks, Challenges)

## Prerequisites
Branch 03 merged. BadgeEvaluator, XP system, ScoreBreakdown exist.

## Rank System

### Rank.kt (domain model)
```kotlin
enum class Rank(
    val level: Int,
    val displayName: String,
    val xpRequired: Int,
    val color: Color,
    val iconRes: Int
) {
    LEARNER(1, "Learner", 0, RankLearner, R.drawable.ic_rank_learner),
    CRUISER(2, "Cruiser", 500, RankCruiser, R.drawable.ic_rank_cruiser),
    ROAD_CAPTAIN(3, "Road Captain", 1500, RankCaptain, R.drawable.ic_rank_captain),
    APEX_DRIVER(4, "Apex Driver", 3500, RankApex, R.drawable.ic_rank_apex),
    STREET_GHOST(5, "Street Ghost", 7000, RankGhost, R.drawable.ic_rank_ghost),
    MIDNIGHT_RACER(6, "Midnight Racer", 15000, RankMidnight, R.drawable.ic_rank_midnight),
    LEGEND(7, "Legend", 30000, RankLegend, R.drawable.ic_rank_legend);

    companion object {
        fun fromXP(xp: Int) = values().lastOrNull { xp >= it.xpRequired } ?: LEARNER
        fun progressToNext(xp: Int): Float {
            val current = fromXP(xp)
            val next = values().getOrNull(current.ordinal + 1) ?: return 1f
            return (xp - current.xpRequired).toFloat() / (next.xpRequired - current.xpRequired)
        }
    }
}
```

### RankBadgeComponent.kt
Reusable composable used everywhere (home, profile, leaderboard, share cards):
```kotlin
@Composable
fun RankBadge(rank: Rank, size: Dp = 40.dp) {
    // Square badge (not circle)
    // Background: rank.color at 15% alpha
    // Border: 1.5dp rank.color
    // Icon: rank icon at 60% of size
    // For LEGEND rank: animated shimmer on border
}
```

---

## StreakSystem.kt
```kotlin
object StreakSystem {
    // Called after each trip saves
    fun updateStreak(user: UserEntity, tripDate: LocalDate): UserEntity {
        val lastDate = user.lastTripDate?.toLocalDate()
        return when {
            lastDate == null -> user.copy(streakDays = 1)
            lastDate == tripDate -> user  // same day, no change
            lastDate == tripDate.minusDays(1) -> user.copy(streakDays = user.streakDays + 1)
            else -> user.copy(streakDays = 1)  // streak broken
        }
    }
    
    // Streak freeze logic:
    // Free users: 1 freeze per week (stored as freezeUsedThisWeek: Boolean)
    // Pro users: unlimited
    // Freeze consumed when: lastTripDate is yesterday-1 AND freeze available
    // FreezesUsed resets every Monday midnight
}
```

Streak push notification: WorkManager daily at 8 PM, checks if user has driven today.
If not: "🔥 Drive today to keep your {n}-day streak alive!"

---

## WeeklyChallenge System

### ChallengeType.kt
```kotlin
sealed class ChallengeType {
    data class ScoreThreshold(val minScore: Int, val requiredTrips: Int) : ChallengeType()
    data class DistanceGoal(val targetKm: Float) : ChallengeType()
    data class CategoryScore(val category: ScoreCategory, val minScore: Int, val requiredTrips: Int) : ChallengeType()
    data class BeatPersonalBest(val currentPB: Int) : ChallengeType()
    data class NightTrips(val count: Int) : ChallengeType()
}

data class WeeklyChallenge(
    val id: String,
    val type: ChallengeType,
    val title: String,
    val description: String,
    val xpReward: Int,
    val progress: Float,   // 0.0–1.0
    val completed: Boolean
)
```

### ChallengeGenerator.kt
```kotlin
// Generates 3 challenges each Monday, personalised to user's weak areas
// Weak area = lowest category score averaged over last 5 trips

fun generateWeeklyChallenges(user: User, recentTrips: List<Trip>): List<WeeklyChallenge> {
    val weakest = recentTrips.findWeakestCategory()
    // Challenge 1: always target weakest category
    // Challenge 2: distance goal (scaled to user's average weekly distance × 1.2)
    // Challenge 3: beat personal best OR streak goal
    // XP rewards: 100, 75, 125
}
```

Challenges stored in Firestore per user, reset Monday 00:00 UTC via Cloud Function.

---

## HomeScreen.kt (Full Build)

```kotlin
// Sections (scrollable, LazyColumn):

// 1. GREETING HEADER
"Hey, {username}" (16sp Rajdhani) — time-aware: "Good morning/evening/night"

// 2. RANK PROGRESS CARD
Surface (Surface color, 1dp MatrixMuted border):
  Row: RankBadge(current) + rank name + "Rank {n} of 7"
  Spacer
  XP progress bar: animated width, MatrixGreen fill, glow
  "{current} / {next} XP  ·  {remaining} XP to {nextRankName}"

// 3. QUICK STATS ROW (2 cards)
Left card: 🔥 {n} day streak (or "Start a streak!")
Right card: "THIS WEEK" — {n} trips · {km} km

// 4. WEEKLY CHALLENGES
Section header "WEEKLY CHALLENGES"
3× ChallengeRow: title + progress bar (partial fill) + "n/total" label

// 5. LAST TRIP CARD
If exists: trip date + distance + score + grade + [View] [Share] buttons
If no trips: SampleTripCard with "Take your first drive" CTA

// 6. FAB (fixed, bottom-right)
Only show if NOT currently tracking
"▶ START TRIP" — MatrixGreen, 56dp, square with 4dp radius
Tapping starts TripTrackingService and navigates to LiveHudScreen
```

---

## RanksScreen.kt (Full Build)

```kotlin
// Sections:

// 1. YOUR RANK — large card with RankBadge, rank name, XP bar, "n/7" label

// 2. RANK LADDER — vertical list of all 7 ranks
Each item:
  completed ranks: ✓ icon, dim text, small rank color pill
  current rank: ► icon, full brightness, rank color glow border on row
  locked ranks: lock icon, TextDim, blurred rank badge

// 3. YOUR BADGES heading + "(earned/30)"

// 4. BADGE GRID — 5 columns
Earned: full color badge icon in Surface2 card, 1dp rank-color border
Locked: grey icon in Surface card, 1dp Border border
Tap any badge → BadgeDetailSheet (bottom sheet)
  Shows: icon (64dp), name, description, earned date or unlock hint

// 5. "VIEW ALL BADGES" → BadgesFullScreen (grid with filter tabs: All / Earned / Locked)
```

---

## WeeklyRecapCard Generator

Runs every Sunday 8 PM via WorkManager if user has driven this week:
```kotlin
// Push notification: "Your weekly recap is ready 📊"
// Generates 1080×1080 Bitmap (same Canvas approach as trip card):
// "WEEK OF {date}" header
// Total: {n} trips · {km}km · avg score {n}
// Best trip: score + grade + date
// Progress to next rank: bar visual
// Streak: fire emoji + count
// "triprank.app" footer
// Saves to gallery + shows in-app notification banner
```

## Acceptance Criteria
- [ ] All 7 ranks display correctly with correct colors and XP thresholds
- [ ] XP bar animates on home screen after trip
- [ ] Streak increments on consecutive daily trips, resets on miss
- [ ] Streak push notification fires at 8 PM if no trip today
- [ ] 3 weekly challenges generated every Monday
- [ ] Challenge progress updates after each trip
- [ ] Badge grid shows earned vs locked correctly
- [ ] Rank-up persists across sessions
- [ ] Weekly recap generated Sunday evening
