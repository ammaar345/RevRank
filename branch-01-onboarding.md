# Branch 01 — Onboarding

## Prerequisites
Branch 00 merged. Theme, auth, navigation shell exist.

## Screens to Build

### OnboardingScreen.kt
3-page pager using `HorizontalPager`. State: `OnboardingViewModel` tracks page, completion flag in DataStore.

**Page 1:** Full-screen black. Center: animated speedometer (Lottie: `anim_speedometer.json`). Below: "YOUR DRIVES, RANKED." (displayLarge, MatrixGreen). Sub-copy (bodyMedium, TextSecondary). Bottom: page dots + NEXT button.

**Page 2:** Sample trip card (non-interactive, hardcoded data: score=87, grade=A, 5 category bars, stats row). Headline: "KNOW HOW YOU DRIVE." Free badge chip ("ALL FREE").

**Page 3:** Two-column comparison. Left column header "FREE FOREVER" (MatrixGreen), 5 checkmark items. Right column header "PRO UNLOCKS" (amber #FFD700), 5 lock items. Bottom: "START DRIVING →" primary button + "Try Pro free for 7 days" text link below it.

### SignInScreen.kt
Black screen. TripRank logo center. "Google" button (white outlined). "Apple" button (white outlined). Both call `AuthRepository.signInWithGoogle/Apple`. On success → UsernamePickerScreen.

### UsernamePickerScreen.kt
Single input. `Share Tech Mono` font. 16-char limit. Alphanumeric + underscore only. Real-time Firestore availability check (debounce 500ms). Green checkmark if available, red X if taken. Shows 3 generated suggestions: `{adjective}_{noun}_{2digits}`. On confirm → VehicleTypeScreen.

### VehicleTypeScreen.kt
4 large tap targets in 2×2 grid: Car / Motorcycle / Van / Truck. Each has an icon and label. Selected = MatrixGreen border glow. Stored in UserEntity. On confirm → PermissionsScreen.

### PermissionsScreen.kt
Two permission rows:
1. Location (Always) — icon, plain-English reason: "We need always-on location to auto-detect when you start driving." → `[GRANT]` button
2. Motion sensors — "We read your accelerometer to score braking and cornering." → `[GRANT]` button

Handle denied: show explanation modal with retry. Show "Why do we need this?" expandable. On both granted (or skip) → mark onboarding complete in DataStore → navigate to HomeScreen.

## DataStore Key
`onboarding_complete: Boolean` — checked in MainActivity before showing onboarding vs home.

## ViewModel
```kotlin
class OnboardingViewModel : ViewModel() {
    val currentPage = MutableStateFlow(0)
    val username = MutableStateFlow("")
    val usernameAvailable = MutableStateFlow<Boolean?>(null)
    val vehicleType = MutableStateFlow<VehicleType?>(null)
    fun checkUsername(name: String) // debounced Firestore check
    fun completeOnboarding()
}
```

## Components to Create (reusable)
- `TripRankButton.kt` — outlined green, ALL CAPS Rajdhani SemiBold, 4dp radius, 48dp height
- `PageDots.kt` — row of 3 dots, active = MatrixGreen filled, inactive = Border outlined
- `SampleTripCard.kt` — the preview card used in onboarding page 2 (reuse in empty home state later)
- `ScoreCategoryBar.kt` — label + 4dp height bar with gradient fill + glow

## Acceptance Criteria
- [ ] Onboarding shows only on first install
- [ ] Skip on any screen works
- [ ] Username uniqueness enforced (Firestore transaction)
- [ ] Both permissions requested with explanation
- [ ] On complete: user document written to Firestore + Room
- [ ] Returning user never sees onboarding again
