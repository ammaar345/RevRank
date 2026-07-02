# Claude Code Prompt — Branch 00: Foundation

## Context
You are rebuilding TripRank, an Android driving tracker app. This is Branch 00 — the foundation. The app is written in Kotlin. You are setting up the project architecture, design system, auth, and navigation shell.

## Your Tasks

### 1. Project Structure (Clean Architecture)
Set up the following package structure:
```
com.revrank/
├── data/
│   ├── local/           (Room database)
│   │   ├── TripRankDatabase.kt
│   │   ├── dao/
│   │   │   ├── TripDao.kt
│   │   │   ├── UserDao.kt
│   │   │   └── BadgeDao.kt
│   │   └── entities/
│   │       ├── TripEntity.kt
│   │       ├── UserEntity.kt
│   │       └── BadgeEntity.kt
│   ├── remote/          (Firestore)
│   │   ├── FirestoreTrip.kt
│   │   ├── FirestoreUser.kt
│   │   └── SyncManager.kt
│   └── repository/
│       ├── TripRepository.kt
│       ├── UserRepository.kt
│       └── AuthRepository.kt
├── domain/
│   ├── model/
│   │   ├── Trip.kt
│   │   ├── User.kt
│   │   ├── Badge.kt
│   │   └── Rank.kt
│   └── usecase/
│       ├── GetCurrentUserUseCase.kt
│       └── SyncTripsUseCase.kt
├── presentation/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   ├── Shape.kt
│   │   └── Theme.kt
│   ├── navigation/
│   │   └── TripRankNavigation.kt
│   └── screens/
│       ├── home/
│       ├── trips/
│       ├── ranks/
│       └── profile/
└── di/
    └── AppModule.kt
```

### 2. Design System Tokens

Create `presentation/theme/Color.kt`:
```kotlin
val Void = Color(0xFF000000)
val Surface = Color(0xFF0A0A0A)
val Surface2 = Color(0xFF111111)
val Surface3 = Color(0xFF1A1A1A)

val MatrixGreen = Color(0xFF00FF41)
val MatrixDim = Color(0xFF00C032)
val MatrixMuted = Color(0xFF004D14)
val MatrixGlow = Color(0x2600FF41)

val ScorePerfect = Color(0xFF00FF41)
val ScoreGreat = Color(0xFF39FF14)
val ScoreGood = Color(0xFFFFD700)
val ScoreOkay = Color(0xFFFF6B00)
val ScorePoor = Color(0xFFFF2D00)

val RankLearner = Color(0xFF888888)
val RankCruiser = Color(0xFF00C032)
val RankCaptain = Color(0xFF0099FF)
val RankApex = Color(0xFF9B59B6)
val RankGhost = Color(0xFF00FFFF)
val RankMidnight = Color(0xFFFF4444)
val RankLegend = Color(0xFFFFD700)

val TextPrimary = Color(0xFFE8E8E8)
val TextSecondary = Color(0xFF888888)
val TextDim = Color(0xFF444444)
val Border = Color(0xFF1E1E1E)
```

Create `presentation/theme/Type.kt`:
Use Google Fonts: Share Tech Mono (display/numbers) and Rajdhani (body).
Add these as downloadable fonts in `res/font/`.
```kotlin
val TripRankTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ShareTechMono,
        fontSize = 96.sp,
        lineHeight = 96.sp
    ),
    displayMedium = TextStyle(
        fontFamily = ShareTechMono,
        fontSize = 64.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ShareTechMono,
        fontSize = 40.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = ShareTechMono,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Rajdhani,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Rajdhani,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Rajdhani,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        letterSpacing = 2.sp
    )
)
```

Create `presentation/theme/Theme.kt` — dark-only, no light theme:
```kotlin
@Composable
fun TripRankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Void,
            surface = Surface,
            primary = MatrixGreen,
            onPrimary = Void,
            secondary = MatrixDim,
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            outline = Border
        ),
        typography = TripRankTypography,
        content = content
    )
}
```

### 3. Room Database

Create `TripRankDatabase.kt` with entities:

**TripEntity:**
```kotlin
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
    val isSynced: Boolean = false
)
```

**UserEntity:**
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val username: String,
    val displayName: String,
    val rank: Int = 1,
    val xp: Int = 0,
    val totalTrips: Int = 0,
    val totalDistanceKm: Float = 0f,
    val streakDays: Int = 0,
    val lastTripDate: Long?,
    val isPro: Boolean = false
)
```

**BadgeEntity:**
```kotlin
@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val earnedAt: Long,
    val badgeType: String
)
```

### 4. Firebase Auth — Google Sign-In

In `AuthRepository.kt`:
```kotlin
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signInWithGoogle(idToken: String): Result<User> { ... }
    suspend fun getCurrentUser(): User? { ... }
    suspend fun claimUsername(uid: String, username: String): Result<Boolean> { ... }
    // Username claim: Firestore transaction to ensure uniqueness
    // Collection: "usernames/{username}" → {uid: String}
}
```

Username uniqueness: Use a Firestore collection `usernames` where document ID = username. A Firestore transaction checks existence before writing. This is atomic and race-condition safe.

### 5. Bottom Navigation Shell

Create `TripRankNavigation.kt` with 4 tabs:
- Home (house icon, route: "home")
- Trips (list icon, route: "trips")  
- Ranks (trophy icon, route: "ranks")
- Profile (person icon, route: "profile")

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Trips : Screen("trips")
    object Ranks : Screen("ranks")
    object Profile : Screen("profile")
}
```

Bottom nav specs:
- Height: 56dp
- Background: Surface (0xFF0A0A0A)
- Top border: 1dp, Border color (0xFF1E1E1E)
- Active indicator: 2dp × 24dp MatrixGreen line ABOVE the icon
- Active icon tint: MatrixDim (0xFF00C032)
- Inactive icon tint: TextDim (0xFF444444)
- No labels

### 6. Hilt DI Setup

Create `AppModule.kt` providing:
- FirebaseAuth instance
- FirebaseFirestore instance
- FirebaseStorage instance
- Room database (singleton)
- All DAOs
- All Repositories

### 7. Dependency Setup (build.gradle.kts)

Add all dependencies:
```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.09.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")

// Navigation
implementation("androidx.navigation:navigation-compose:2.8.0")

// Hilt
implementation("com.google.dagger:hilt-android:2.51.1")
kapt("com.google.dagger:hilt-compiler:2.51.1")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-crashlytics-ktx")

// Google Sign-In
implementation("com.google.android.gms:play-services-auth:21.0.0")

// Lottie
implementation("com.airbnb.android:lottie-compose:6.4.0")

// Coil (image loading)
implementation("io.coil-kt:coil-compose:2.6.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

// RevenueCat (subscriptions)
implementation("com.revenuecat.purchases:purchases:7.5.0")
implementation("com.revenuecat.purchases:purchases-ui:7.5.0")
```

## Acceptance Criteria
- [ ] App builds and runs without crash
- [ ] Google Sign-In works and stores user in Firestore
- [ ] Username claim is atomic (no duplicates possible)
- [ ] Room DB initialises with correct schema
- [ ] Bottom nav navigates between 4 placeholder screens
- [ ] Design tokens applied (dark theme, correct colors and fonts)
- [ ] Firebase project connected (google-services.json in place)

## Notes for Claude Code
- Use Kotlin 1.9.x and Compose Compiler compatible version
- minSdk: 26 (Android 8.0) — covers 95% of target devices
- targetSdk: 34
- Enable edge-to-edge display
- Splash screen: full black background, TripRank wordmark in MatrixGreen, matrix rain Lottie animation
