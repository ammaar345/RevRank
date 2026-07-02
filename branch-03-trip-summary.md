# Branch 03 — Trip End & Score Reveal

## Prerequisites
Branch 02 merged. Trips save to Room with ScoreBreakdown.

## TripEndScreen.kt

Navigate here immediately after trip ends. Receives `tripId: String` as nav arg. Loads trip from Room. Full-screen, dark, cinematic.

### Animation Sequence (run sequentially with delay chaining)

```kotlin
// Phase 1: 0–800ms — Matrix rain
// Draw random ASCII chars (from MatrixRainChars) in Share Tech Mono
// falling into a 120×80dp box at center
// Use Canvas + animateFloatAsState on yOffset per column

// Phase 2: 800–1300ms — Score reveal
// Characters resolve: animate each char to its digit
// Use animateIntAsState(targetValue=score, animationSpec=tween(500))

// Phase 3: 1300–1600ms — Grade letter drops
// Grade letter (S/A/B/C/D) slides in from top with alpha 0→1
// Apply text shadow glow: color = grade color, blur 20dp

// Phase 4: 1600ms+ — Stats reveal, staggered
// Each row fades in with 100ms delay between them
// ScoreCategoryBars animate width from 0 to final value
```

Grade mapping:
```kotlin
fun Int.toGrade() = when {
    this >= 90 -> "S"
    this >= 75 -> "A"
    this >= 55 -> "B"
    this >= 35 -> "C"
    else       -> "D"
}
fun Int.toGradeColor() = when {
    this >= 90 -> ScorePerfect
    this >= 75 -> ScoreGreat
    this >= 55 -> ScoreGood
    this >= 35 -> ScoreOkay
    else       -> ScorePoor
}
```

### Layout (after animation)

```
StatusBar (transparent)

[MATRIX RAIN → SCORE NUMBER, 64sp Share Tech Mono, grade color]
[GRADE LETTER, 64sp, glowing]

HORIZONTAL DIVIDER (1dp, Border color)

ACCELERATION  [████████░░] 84
BRAKING       [██████░░░░] 71
CORNERING     [█████████░] 90
SMOOTHNESS    [████████░░] 83
CONSISTENCY   [███████░░░] 76
                    ↑ Compare arrow: "↑9 above your avg" if better

HORIZONTAL DIVIDER

STATS ROW: [22km] [41min] [84 km/h max]
           [DIST] [TIME]  [TOP SPEED]

COMPARISON ROW (if same route driven before):
"↑ 5 pts vs last time on this route"

HORIZONTAL DIVIDER

[+62 XP  ↑] (floats up, green, then settles — animateOffset)

SHARE button (outlined green, full width)
VIEW HISTORY button (text only, dim)
```

### Badge Earned Overlay
If trip triggers a badge unlock, show AFTER score animation:
```
Full screen dark overlay (alpha 0.9)
Badge icon scales from 0.3 to 1.0 (spring animation)
Particle burst: 24 small MatrixGreen dots radiate outward (Canvas)
Badge name (24sp, MatrixGreen)
Badge description (14sp, TextSecondary)
"TAP TO CONTINUE" (12sp, TextDim, pulsing alpha)
```
Check badges in `BadgeEvaluator.kt` — runs after every trip end.

### BadgeEvaluator.kt
```kotlin
object BadgeEvaluator {
    fun evaluate(trip: Trip, user: User, allTrips: List<Trip>): List<BadgeType> {
        val newBadges = mutableListOf<BadgeType>()
        // Check each badge condition against trip + history
        // Return only newly earned badges (not previously awarded)
        return newBadges
    }
}

enum class BadgeType(val id: String, val displayName: String, val description: String) {
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
    WEEKEND_WARRIOR("weekend_warrior", "Weekend Warrior", "5 weekend trips")
    // ... 14 more
}
```

### ShareCardGenerator.kt
```kotlin
// Renders a 1080×1080px Bitmap using Canvas
// Called from coroutine, returns Bitmap

fun generateTripCard(trip: Trip, user: User): Bitmap {
    // Background: #000000
    // Scan line texture: draw horizontal lines every 8px, alpha 8, color black
    // Top-left: "TRIPRANK" wordmark (Share Tech Mono, 32sp, MatrixGreen)
    // Username: (Rajdhani SemiBold, 28sp, TextPrimary)
    // Rank badge: rank name + rank color rectangle
    // Horizontal rule
    // Score: (Share Tech Mono, 120sp, grade color) — left aligned
    // Grade: (Share Tech Mono, 120sp, grade color) — right aligned
    // Horizontal rule
    // Stats: 3 columns (distance, time, max speed)
    // 3 score bars (braking, acceleration, cornering only — 3 fits)
    // Horizontal rule
    // Bottom: "triprank.app" (Rajdhani, 18sp, TextSecondary)
    // Green left border: 8px wide strip on left edge, MatrixGreen
}

// Then call: 
// FileProvider to save Bitmap
// startActivity(Intent.ACTION_SEND with image Uri)
```

## XP Calculation
```kotlin
fun calculateXP(trip: Trip, streakBonus: Int): Int {
    val base = trip.score * (trip.distanceKm / 10f).coerceAtLeast(1f).toInt()
    val perfectBonus = if (trip.score == 100) 50 else 0
    val streakMultiplier = 1 + (streakBonus * 0.1f)
    return ((base + perfectBonus) * streakMultiplier).toInt()
}
```

Write XP to UserEntity, check for rank-up after every trip.

## Rank-Up Screen
If rank increases after XP added:
```
Full screen. New rank name in massive text.
Lottie animation: `anim_rankup.json` (commission or use free particle Lottie)
Rank badge scales in from 0
"YOU ARE NOW [RANK NAME]" (Share Tech Mono, 28sp, rank color)
Auto-dismiss after 3s or tap
```

## Acceptance Criteria
- [ ] Matrix rain animation plays on every trip end
- [ ] Score counts up correctly
- [ ] Grade letter color matches score tier
- [ ] Category bars animate in sequentially
- [ ] Personal comparison shows correctly
- [ ] Badge overlay shows for newly earned badges only
- [ ] Share card generates at 1080×1080 with correct layout
- [ ] XP calculated and written to user
- [ ] Rank-up screen triggers when rank threshold crossed
