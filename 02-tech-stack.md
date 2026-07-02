# 02 — Tech Stack

## Decision Principles
- Minimize rewrites of existing code — extend, don't replace where possible
- The app is Android-native (Kotlin/Java assumed from Play Store listing)
- All new infrastructure should be serverless/low-cost until scale demands otherwise
- Backend must support real-time leaderboards without prohibitive cost

---

## Mobile (Android)

| Layer | Technology | Rationale |
|-------|-----------|-----------|
| Language | **Kotlin** | Modern Android standard, coroutines for async GPS |
| UI Framework | **Jetpack Compose** | Declarative UI, animations are first-class, replaces XML layouts |
| Navigation | **Compose Navigation** | Type-safe, backstack handled, deep link support |
| Architecture | **MVVM + Clean Architecture** | Separates concerns, testable, industry standard |
| DI | **Hilt** | Less boilerplate than Dagger, Compose-compatible |
| Local DB | **Room** | Trip history, offline-first, SQLite wrapper |
| GPS | **Fused Location Provider API** | Google's battery-optimised location, handles indoor/outdoor |
| Motion Sensors | **SensorManager** (accelerometer + gyroscope) | G-force, cornering, braking detection |
| Background Service | **Foreground Service + WorkManager** | Trip auto-detection while app is backgrounded |
| Network | **Retrofit + OkHttp** | Battle-tested, interceptors for auth |
| State Management | **StateFlow + ViewModel** | Reactive, lifecycle-aware |
| Image Generation | **Canvas API + Compose DrawScope** | Render shareable trip cards natively |
| Animations | **Compose Animated* APIs + Lottie** | Smooth rank-up animations, badge unlocks |
| Auth | **Firebase Auth** (Google + Apple Sign-In) | Fixes the username loss bug permanently |
| Analytics | **Firebase Analytics** | Free, deep funnel tracking |
| Crash Reporting | **Firebase Crashlytics** | Free crash monitoring |

---

## Backend

| Layer | Technology | Rationale |
|-------|-----------|-----------|
| Platform | **Firebase (primary)** | Free tier is generous, real-time DB for leaderboards |
| Database | **Firestore** | Real-time sync, offline support, scales automatically |
| Auth | **Firebase Auth** | Google/Apple/email, handles token refresh |
| Storage | **Firebase Storage** | Trip images, profile photos |
| Functions | **Cloud Functions (Node.js/TypeScript)** | Score calculation, leaderboard updates, notifications |
| Push Notifications | **Firebase Cloud Messaging (FCM)** | Free, reliable |
| Leaderboard | **Firestore + Cloud Functions** | Weekly score aggregation via scheduled function |
| Subscriptions | **RevenueCat** | Handles Play Store + future App Store billing, free up to $2.5k MRR |
| A/B Testing | **Firebase Remote Config** | Test paywall placements, onboarding variants |

---

## iOS (Future — Phase 3)

When iOS is built, use:
- **SwiftUI** (mirrors Compose paradigm)
- Same Firebase backend (zero changes)
- **StoreKit 2** for subscriptions (RevenueCat abstracts this)

---

## Data Architecture

### Trip Object (Firestore + Room)
```
Trip {
  id: String
  userId: String
  startTime: Timestamp
  endTime: Timestamp
  distance: Float (km)
  maxSpeed: Float (km/h)
  avgSpeed: Float (km/h)
  score: Int (0–100)
  scoreBreakdown: {
    acceleration: Int
    braking: Int
    cornering: Int
    smoothness: Int
    speedConsistency: Int
  }
  gpsPoints: List<LatLng>      // stored locally, synced on Pro
  sensorData: List<SensorEvent> // stored locally only
  routeName: String?
  shareImageUrl: String?
  badges: List<String>          // badges earned on this trip
}
```

### User Object
```
User {
  uid: String
  username: String
  displayName: String
  avatarUrl: String?
  rank: Int (1–7)
  xp: Int
  totalTrips: Int
  totalDistance: Float
  weeklyScore: Int              // used for leaderboard
  badges: List<Badge>
  streakDays: Int
  lastTripDate: Timestamp
  isPro: Boolean
  proExpiry: Timestamp?
}
```

---

## Scoring Algorithm

The trip score (0–100) is calculated on-device in a Cloud Function mirror:

```
score = weighted_average(
  acceleration_score  × 0.25,   // how smooth are accelerations
  braking_score       × 0.25,   // hard braking events penalised
  cornering_score     × 0.20,   // lateral G-force smoothness
  speed_consistency   × 0.15,   // variance from avg speed
  stop_smoothness     × 0.15    // smooth to a stop vs slamming
)

// Penalties
score -= hard_braking_events × 3
score -= rapid_acceleration_events × 2
score -= speed_above_130kmh_events × 5

score = clamp(score, 0, 100)
```

---

## DevOps

| Tool | Use |
|------|-----|
| **GitHub** | Version control, branch strategy |
| **GitHub Actions** | CI: lint, test, build APK on every PR |
| **Firebase App Distribution** | Beta builds to testers |
| **Gradle** | Build system |
| **Play Store Internal Track** | Staged rollouts |

---

## Branch Strategy (Git)

```
main                    ← production releases only
develop                 ← integration branch
branch/00-foundation    ← first branch off main
branch/01-onboarding    ← branches off develop after 00 merges
branch/02-trip-tracking
...etc
```

Every branch gets a PR into `develop`. `develop` merges into `main` for releases.

---

## Cost Estimate (Month 1–3, Free Tier)

| Service | Free Tier Limit | Expected Usage |
|---------|----------------|----------------|
| Firebase Auth | 10k/month | Well within |
| Firestore reads | 50k/day | ~10k/day at 500 DAU |
| Firestore writes | 20k/day | ~5k/day |
| Cloud Functions | 2M invocations/month | Fine |
| RevenueCat | Free to $2.5k MRR | Free initially |
| Firebase Storage | 5GB | Fine for cards |
| **Total cost** | **$0/month** | Until ~5k DAU |
