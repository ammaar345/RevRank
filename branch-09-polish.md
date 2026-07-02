# Branch 09 — Polish, Animations & Final QA

## Prerequisites
All branches 00–08 merged. Feature-complete. This branch adds finish.

---

## Haptics

```kotlin
// HapticManager.kt — inject everywhere needed
class HapticManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val vibrator = context.getSystemService(VibrationManager::class.java)
    
    fun tripStart()    = vibrate(VibrationEffect.createOneShot(80, 180))
    fun tripEnd()      = vibrate(VibrationEffect.createWaveform(longArrayOf(0,60,40,60), -1))
    fun rankUp()       = vibrate(VibrationEffect.createWaveform(longArrayOf(0,80,50,80,50,120), -1))
    fun badgeUnlock()  = vibrate(VibrationEffect.createOneShot(120, 220))
    fun scoreReveal()  = vibrate(VibrationEffect.createOneShot(40, 100))
    fun buttonPress()  = vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
    fun error()        = vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
}
```

Trigger points:
- Trip auto-detected: `tripStart()`
- [END TRIP] tapped: `tripEnd()`
- Score revealed (after animation): `scoreReveal()`
- Badge overlay appears: `badgeUnlock()`
- Rank-up screen: `rankUp()`
- Any primary button tap: `buttonPress()`

---

## Loading & Skeleton States

Replace all loading spinners with skeleton screens:
```kotlin
// SkeletonBox.kt — shimmer effect
@Composable
fun SkeletonBox(modifier: Modifier) {
    val shimmerColors = listOf(Surface2, Surface3, Surface2)
    val transition = rememberInfiniteTransition()
    val x by transition.animateFloat(0f, 1000f, infiniteRepeatable(tween(1200)))
    Box(modifier.background(
        Brush.linearGradient(shimmerColors, start = Offset(x - 400, 0f), end = Offset(x, 0f))
    ))
}
```

Screens needing skeletons: TripHistory (trip row skeletons), Leaderboard (5 row skeletons), AnalyticsScreen (chart area skeleton), ProfileScreen (stats row skeleton).

---

## Empty States

Every list/screen with no data needs an illustrated empty state. No blank screens.

```kotlin
@Composable
fun EmptyState(icon: ImageVector, headline: String, subtext: String, cta: Pair<String, () -> Unit>? = null) {
    Column(horizontalAlignment = CenterHorizontally) {
        Icon(icon, tint = MatrixMuted, modifier = Modifier.size(64.dp))
        Spacer(16.dp)
        Text(headline, style = MaterialTheme.typography.headlineLarge, color = TextSecondary)
        Text(subtext, style = MaterialTheme.typography.bodyMedium, color = TextDim, textAlign = Center)
        cta?.let { TripRankButton(it.first, onClick = it.second) }
    }
}
```

Empty state copy:
- No trips: icon=speedometer, "NO TRIPS YET", "Take your first drive to get your score.", CTA="START TRACKING"
- No badges: icon=trophy, "NO BADGES YET", "Complete trips to earn your first badge."
- No friends: icon=person+, "NO FRIENDS YET", "Challenge someone — share your trip card."
- Leaderboard loading failed: icon=wifi-off, "COULDN'T LOAD", "Check your connection.", CTA="RETRY"

---

## Error States

```kotlin
// ErrorState.kt — used when network calls fail
@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    // Brief message + RETRY button
    // Never show raw exception messages to users
    // Log to Crashlytics, show friendly message
}

// Friendly messages:
// Network error → "Couldn't connect. Check your signal."
// Auth error → "Session expired. Sign in again."
// Generic → "Something went wrong. Try again."
```

---

## Transition Animations

```kotlin
// In TripRankNavigation.kt — apply to all routes:
composable(
    route = Screen.TripDetail.route,
    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(300)) },
    exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(300)) }
)

// Trip card → Trip detail: shared element transition on score number
// Use SharedTransitionLayout (Compose 1.7+)
SharedTransitionLayout {
    // In trip list: Modifier.sharedElement(state, "score_${trip.id}")
    // In trip detail: same modifier
}
```

---

## Scan Line Canvas Modifier

```kotlin
fun Modifier.scanLines(alpha: Float = 0.03f): Modifier = this.drawWithContent {
    drawContent()
    val lineSpacing = 4.dp.toPx()
    var y = 0f
    while (y < size.height) {
        drawLine(Color.Black.copy(alpha = alpha), Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += lineSpacing
    }
}
// Apply to: PaywallScreen hero, TripEndScreen, HUD screen
```

---

## MatrixRain Canvas Component

```kotlin
// MatrixRainCanvas.kt — used in splash, paywall hero, score reveal
@Composable
fun MatrixRain(modifier: Modifier, density: Int = 20) {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%".toList()
    // Create `density` columns, each with random characters falling
    // Each column: yOffset animates from -height to height with random duration 800–2000ms
    // Characters: Share Tech Mono, 12sp, MatrixGreen varying alpha (head = bright, tail = fading)
    // Use Canvas + drawText for each character
    // Loop with rememberInfiniteTransition
}
```

---

## App Icon

Design brief for `ic_launcher.xml` (adaptive icon):
- Background: pure black (#000000)
- Foreground: stylised speedometer needle, sweeping right, matrix green
- The needle should double-read as a "T" (for TripRank) — bottom horizontal bar of needle + vertical mast
- No text in icon
- Foreground should work at 48dp

Create all required sizes: mdpi/hdpi/xhdpi/xxhdpi/xxxhdpi + adaptive icon XML.

---

## Play Store Screenshots (6 required)

Generate using screenshot tool or ScreenshotMaker. Required screens:
1. Home screen (streak + challenges visible)
2. Live HUD (speed + green glow border)
3. Trip End score reveal (mid-animation or post-animation)
4. Ranks screen (badge grid visible)
5. Route replay (Pro — colored polyline on dark map)
6. Share card mockup (trip card on phone screen)

Frame device: Pixel 8 frame, black background, add caption text below each.
Caption style: Share Tech Mono, MatrixGreen, ALL CAPS, 1–4 words.

---

## Accessibility

```kotlin
// Minimum contrast: 4.5:1 for all text
// TextPrimary (#E8E8E8) on Surface (#0A0A0A) = 17:1 ✓
// MatrixDim (#00C032) on Void (#000000) = 5.7:1 ✓
// TextSecondary (#888888) on Void = 4.6:1 ✓ (just passes)
// TextDim (#444444) on Void = 2.0:1 ✗ — only use for decorative/non-critical text

// Touch targets: all interactive elements minimum 48×48dp
// Score bars: add contentDescription="Category score: X out of 100"
// Icons without text: add contentDescription
// Custom Canvas components: add semantics { }
```

---

## Final QA Checklist

**Functional:**
- [ ] Full new user flow: install → onboard → 3 trips → Pro paywall shown
- [ ] Auth persists across: reinstall, clear cache, new device
- [ ] Trip auto-detects correctly on 3 different routes
- [ ] Share card generates and shares without crash
- [ ] Deep link from share card opens correct screen
- [ ] Rank-up triggers exactly once per rank level
- [ ] Streak resets after 2-day gap (no trip yesterday or today)
- [ ] Pro purchase, use features, cancel, features disabled gracefully
- [ ] Restore purchase works

**Performance:**
- [ ] App cold start < 2 seconds
- [ ] Home screen renders < 300ms
- [ ] Trip end screen animations run at 60fps (no jank in GPU profiler)
- [ ] Memory: no leaks (check with LeakCanary in debug build)
- [ ] Battery < 3% per hour tracking

**Devices to test:**
- Pixel 6a (Android 14, reference)
- Samsung Galaxy A54 (Android 13, aggressive OEM battery)
- Samsung Galaxy S23 (Android 14, high-end)
- Older device: Pixel 4a (Android 13, lower-spec)

**Edge cases:**
- [ ] Trip started, phone dies mid-trip → on recharge, trip partially saved
- [ ] No internet during trip → saves locally, syncs later
- [ ] Username with special chars rejected
- [ ] Score of 0 displays correctly (no division by zero)
- [ ] User with 0 trips: empty states shown, no crashes
