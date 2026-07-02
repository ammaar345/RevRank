# Branch 05 — Social Layer

## Prerequisites
Branch 04 merged. Share card generator, user profiles, badges, rank all exist.

## Deep Links Setup

Use Firebase Dynamic Links (or App Links with intent filters):
```xml
<!-- AndroidManifest.xml -->
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW"/>
    <data android:scheme="https" android:host="triprank.app"/>
</intent-filter>
```

Link patterns:
- `triprank.app/trip/{tripId}` — view a shared trip
- `triprank.app/challenge/{challengeId}` — accept a route challenge
- `triprank.app/profile/{username}` — view public profile
- `triprank.app/ref/{userId}` — referral link

Handle in `MainActivity.handleDeepLink(intent)`.

---

## Friend Challenge System

### ChallengeRepository.kt
```kotlin
data class RouteChallenge(
    val id: String,
    val creatorUid: String,
    val creatorUsername: String,
    val creatorScore: Int,
    val routeHash: String,    // hash of start/end GPS coords ± 500m radius
    val tripId: String,
    val createdAt: Timestamp,
    val expiresAt: Timestamp, // 7 days
    val acceptances: List<ChallengeAcceptance>
)

data class ChallengeAcceptance(
    val uid: String,
    val username: String,
    val score: Int?,           // null until they drive it
    val completedAt: Timestamp?
)
```

**Create challenge:** From TripDetailScreen, [CHALLENGE A FRIEND] button.
- Generates `RouteChallenge` in Firestore
- Creates dynamic link: `triprank.app/challenge/{id}`
- Opens share sheet with: "Beat my {score} on this route! [link]"

**Accept challenge:** Deep link opens `ChallengeDetailScreen`:
- Shows challenger name, their score, route map thumbnail (if Pro; blurred if free)
- [ACCEPT] stores challenge acceptance in Firestore
- Sends push notification to creator: "{username} accepted your challenge!"
- When user next drives same route (routeHash match): auto-links trip to challenge
- After trip: comparison screen overlay shows challenger score vs yours

**Route matching:** 
```kotlin
fun routeHash(startLat: Double, startLng: Double, endLat: Double, endLng: Double): String {
    // Round to 2 decimal places (~1km precision) then hash
    val s = "${(startLat * 100).roundToInt()}_${(startLng * 100).roundToInt()}"
    val e = "${(endLat * 100).roundToInt()}_${(endLng * 100).roundToInt()}"
    return "${s}_${e}".md5()
}
```

---

## LeaderboardScreen.kt

Tabs: [GLOBAL] [FRIENDS (Pro)]

**Global tab:**
- Firestore query: `weeklyScores` collection, ordered by `avgScore desc`, limit 100
- `weeklyScores/{uid}` updated by Cloud Function after each trip
- Current user's rank highlighted (sticky row if off-screen)
- Refresh on pull-to-refresh
- Each row: rank number + username + rank badge + avg score this week

**Friends tab (Pro gate):**
- Soft paywall if not Pro
- Friends list = Firestore `users/{uid}/friends` subcollection
- Query their weeklyScores and merge with global for a personal table

**Cloud Function (scheduled, runs hourly):**
```javascript
// functions/src/updateWeeklyScores.ts
// Aggregates trips from past 7 days per user
// Calculates avg score weighted by distance
// Writes to weeklyScores/{uid}
// Resets every Monday 00:00 UTC
```

---

## PublicProfileScreen.kt

Route: `triprank.app/profile/{username}` and in-app navigation.

```
Avatar (square, 64dp, rank-color border)
Username (20sp Share Tech Mono)
Rank badge + rank name
─────────────────────────────
{avg} avg  |  {km}km  |  {n} trips
─────────────────────────────
Recent badges (4, then "see all")
─────────────────────────────
[CHALLENGE THIS DRIVER]  ← creates challenge vs their best recent trip
[SHARE PROFILE]          ← share link
```

Profile is public to anyone with the link. No private mode (simplicity).

---

## Referral System

### ReferralManager.kt
```kotlin
// Each user has a referral code = first 8 chars of uid
// Link: triprank.app/ref/{code}
// When new user installs via ref link:
//   - new user gets 30-day Pro trial (instead of 7)
//   - referrer gets 30 days Pro added to their account
//   - tracked in Firestore: users/{uid}/referrals subcollection
// Max benefit: 12 months free per year
// Cloud Function handles credit when referral installs + completes first trip
```

Show in ProfileScreen: "INVITE FRIENDS" → ReferralScreen:
- Unique link displayed
- Copy button
- Share button (pre-written message: "I've been tracking my drives on TripRank. Join me — triprank.app/ref/{code}")
- "You've referred {n} friends" counter
- "Pro credits earned: {n} months"

---

## Redesigned Share Cards

Three card types, all 1080×1080 Canvas:

**Trip Card** (existing — polish):
- Left green border stripe (12px)
- Scanline texture overlay (opacity 3%)
- Username + rank badge top-left
- Score (huge) + grade letter center
- 3 category mini-bars
- Stats row bottom
- "triprank.app" footer

**Weekly Recap Card** (new):
- Header: "WEEK OF {Mon dd} – {Sun dd MMM}"
- Large center: "{n} trips  ·  {km}km"
- Avg score for week with grade letter
- Rank progress: "↑ {n} XP this week"
- Streak count
- Best trip score + date

**Badge Card** (new):
- Badge icon full-size center (256dp)
- Badge name (Share Tech Mono, 32sp)
- "Earned by {username}" (Rajdhani, 16sp, TextSecondary)
- Rank badge small top-right
- "triprank.app — Can you earn it?"

---

## Acceptance Criteria
- [ ] Deep links open correct screen on both fresh install and existing install
- [ ] Challenge creation generates valid dynamic link
- [ ] Challenge accepted notification fires within 30 seconds
- [ ] Route matching correctly links trips to challenges within ~1km tolerance
- [ ] Global leaderboard loads within 2s, refreshes on pull
- [ ] Friends tab shows Pro gate for free users
- [ ] Referral credits applied correctly via Cloud Function
- [ ] All 3 share card types render at correct 1080×1080
