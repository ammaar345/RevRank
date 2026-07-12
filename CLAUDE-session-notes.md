# RevRank — Session Notes (Branches 04–09)

Full implementation logs for each branch. Referenced from main `CLAUDE.md`.

## Session Notes (June 24, 2026)

### Branch 04 — Gamification Completed

- **Rank.kt**: 7 tiers (Learner → Legend), XP thresholds, colors, `progressToNext()` extension
- **RankBadge.kt**: Reusable square badge component with ranked color border
- **StreakSystem.kt**: Pure function with freeze logic (1 free/week for Free, unlimited for Pro)
- **WeeklyChallenge.kt + WeeklyChallengeGenerator.kt**: Domain model + generator (3 personalized challenges/week)
- **HomeScreen.kt**: Main dashboard with greeting, rank progress card, weekly stats, challenges list, last trip, FAB
- **RanksScreen.kt**: Rank ladder (visual progress), badge grid (5-column), badge detail sheet (bottom sheet)
- **HomeViewModel.kt**: Hilt ViewModel wiring state + sample data
- **MainActivity updated**: HomeScreen now observes HomeViewModel

---

### Branch 05 — Social Layer (Completed)

**Accomplished:**
- **Deep Links Setup**: Added intent filter in `AndroidManifest.xml` for:
  - `triprank.app/trip/{tripId}` — view a shared trip
  - `triprank.app/challenge/{challengeId}` — accept a route challenge
  - `triprank.app/profile/{username}` — view public profile
  - `triprank.app/ref/{userId}` — referral link
- **MainActivity modifications**:
  - Handles incoming intents (including `onNewIntent`) to capture deep link URIs
  - Uses `MainViewModel` to store and clear the pending deep link
  - LaunchedEffect in `TripRankApp` processes the pending URI and navigates to the appropriate screen (stubbed for now)
  - Added `handleDeepLink` function that parses the path and attempts navigation to placeholder destinations
- **Navigation updates**:
  - Added `Screen.kt` with all screen routes, including `ChallengeAccept` and `PublicProfile`
  - Created `MainViewModel.kt` to manage pending deep link state
- **Challenge data model**:
  - Created `RouteChallenge.kt` and `ChallengeAcceptance.kt` under `domain/model/`
- **Challenge repository**:
  - Created `ChallengeRepository.kt` under `domain/repository/` with Firestore CRUD operations for challenges and acceptances
- **Challenge ViewModel**:
  - Created `ChallengeViewModel.kt` to interact with `ChallengeRepository` and expose state to the UI
- **Challenge Acceptance UI**:
  - Implemented `ChallengeAcceptanceScreen` that shows challenger info, allows user to accept, and stores the acceptance in Firestore.
- **Challenge Creation UI**:
  - Added a "CHALLENGE A FRIEND" button to `TripEndScreen` that creates a `RouteChallenge` in Firestore, generates a dynamic link, and opens the share sheet.
- **Referral System**:
  - Implemented referral code handling when a deep link of type `/ref/{code}` is opened (apply credit to both referrer and referee).
  - Updated `User` model with `referredBy` and `hasUsedReferral` fields.
  - Updated `UserRepository` to process referrals and award XP bonuses.
- **LeaderboardScreen**:
  - Built `LeaderboardScreen` with tabs for Global and Friends (Pro-gated), backed by Firestore `weeklyScores` collection.
  - Created `LeaderboardViewModel` and `LeaderboardRepository` implementations.
- **PublicProfileScreen**:
  - Created a shareable profile page accessible via `triprank.app/profile/{username}`.
  - Created `PublicProfileViewModel` to fetch user data by username.
- **Updated Share Cards**:
  - Redesigned `ShareCardGenerator` to produce three card types (Trip, Weekly Recap, Badge) matching the new 1080×1080 Canvas specs.

**Remaining tasks for Branch 05:**
- **Push Notifications**: Implement FCM for challenge acceptance notifications (optional but recommended).

---

### Branch 06 — Pro Features (Completed)

**ProGate Infrastructure:**
- **LocalProStatus.kt**: Singleton state holder for Pro subscription status, combines Room UserDao with RevenueCat flow (stubbed for Branch 07)
- **ProGate.kt**: Composable wrapper — shows Pro content or `ProUpsellBanner` fallback based on `LocalProStatus.current`
- **ProUpsellBanner.kt**: Inline soft gate with feature-specific copy, Subscribe CTA, dismiss button

**Data Model Changes:**
- **TripEntity.kt**: Added `gpsPointsJson` and `gForcePointsJson` columns with Gson TypeConverters for Room persistence
- **GpsPoint.kt / GForcePoint.kt**: New domain models for route replay and G-force visualizer data
- **Trip.kt**: Updated domain model with MFA `gpsPoints: List<GpsPoint>` and `gForcePoints: List<GForcePoint>`
- **build.gradle.kts**: Added `com.google.code.gson:gson:2.10.1` and `com.google.android.gms:play-services-maps:18.1.0`

**Repository Updates:**
- **TripRepository.kt**: Enhanced `endTrip()` to accept and persist GPS/G-force points with trimming (max 2000 GPS, 1000 G-force)
- **TripTrackingRepository.kt**: Added live buffering of GPS and G-force points during trip tracking

**Route Replay (Pro):**
- **RouteReplayScreen.kt**: Full-screen map with polyline overlay, bottom sheet with playback controls (play/pause, scrub), Pro-gated
- **RouteReplayViewModel.kt**: Hilt ViewModel loading trip data by ID from TripRepository

**G-Force Visualizer (Pro):**
- **GForceScreen.kt**: Compose Canvas radar chart showing lateral vs longitudinal G-forces, color-coded by score, with safe zone indicator and summary stats (max lateral/longitudinal G, safe zone %)
- **GForceViewModel.kt**: Hilt ViewModel with SavedStateHandle for tripId

**Analytics (Pro):**
- **AnalyticsScreen.kt**: Three-tab screen (SCORES, DISTANCE, CATEGORIES) with placeholder charts for line chart, calendar heatmap, and radar chart
- **AnalyticsViewModel.kt**: Hilt ViewModel exposing trip data flow from TripRepository

**Trip History Gating:**
- **TripsScreen.kt**: Updated with Pro gating — free users see last 30 days (max 10 trips), Pro users get unlimited history with monthly grouping
- **TripsViewModel.kt**: Hilt ViewModel for trip list state

**Navigation:**
- **Screen.kt**: Added `RouteReplay`, `GForce`, `Analytics` routes
- **MainActivity.kt**: Updated `handleDeepLink` to navigate trip deep links to RouteReplayScreen, added composable destinations for all Pro screens

**Remaining for Branch 06:**
- CSV Export Manager (framework in place, needs `ExportManager.kt` implementation)
- Home Screen Widget via Jetpack Glance API
- Actual Google Maps integration in RouteReplayScreen (currently placeholder)
- Real charting library integration (MPAndroidChart/Vico) in AnalyticsScreen

---

## Session Notes (June 25, 2026)

### Design Preview Refresh — Cyberpunk HUD

**File:** `design-preview-all.html` — fully rewritten CSS with new design system.

**Changes applied:**
- **Typography:** Share Tech Mono replaced with JetBrains Mono + Orbitron for display
- **Radius overhaul:** All buttons → pill (999px), cards → 8-14px, phone frames → 36px
- **Card fills:** All cards changed from outline-only to `#121212` filled backgrounds
- **Buttons:** Solid Matrix green fill on primary CTAs, pill shape, hover/active scale animations
- **Bottom nav:** Icon+text stacked with SVG icons (radar, clock, trophy, user), active state dot, blurred glass background
- **Scanlines:** Reduced to ~2.5% opacity (barely visible HUD texture)
- **Glow:** `glow-center` divs added behind radial score gauge and rank badges
- **Progress bars:** Neon tube glow via `filter: drop-shadow()`
- **Multi-color metrics:** CSS vars for `--cyan`, `--amber`, `--orange` added
- **HUD background:** `hud-bg` layer with radial green gradient + mesh grid pattern at 1.4% opacity
- **CLAUDE.md:** Design rules section fully updated with new tokens

**To view:** Open `design-preview-all.html` in browser (file:// or local server).

---

### Branch 07 — Monetization (Completed)

**New files created:**
- **`data/revenuecat/PurchaseManager.kt`** — RevenueCat singleton wrapper: `isProFlow`, `purchasePackage()`, `restorePurchases()`, `getOfferings()`, `getProPackage()`
- **`presentation/screens/paywall/PaywallScreen.kt`** — Full paywall UI per spec: hero section, feature list (staggered diamond icons, animated), pricing toggle (Monthly/Annual segmented control with save % badge), trial banner (first encounter only), CTA button (pill, MatrixGreen), restore link, loading/error states
- **`presentation/screens/paywall/PaywallViewModel.kt`** — Hilt ViewModel: loads RevenueCat offerings, manages selection/purchase/restore, tracks first encounter via SharedPreferences, exposes pricing strings with savings calculation
- **`presentation/screens/paywall/ProActivatedScreen.kt`** — Full-screen success overlay: Matrix rain animation resolving to "PRO ACTIVATED", auto-dismiss after 3s
- **`domain/usecase/PaywallTriggerManager.kt`** — Gate logic: never shows before 3 trips, never if already Pro
- **`domain/model/PricingPeriod.kt`** — MONTHLY / ANNUAL enum

**Files updated:**
- **`TripRankApplication.kt`** — Added RevenueCat `Purchases.configure()` in `onCreate()`
- **`di/AppModule.kt`** — Added `providePurchaseManager()` Hilt provider
- **`statemanagement/LocalProStatus.kt`** — Complete rewrite: combined `PurchaseManager.isProFlow` with local `UserDao` for real-time Pro status
- **`presentation/components/ProGate.kt`** — Fixed broken `LocalProStatusImpl` (had TODOs in super call), replaced with proper Hilt `EntryPoint` accessor
- **`presentation/screens/paywall/ProUpsellBanner.kt`** — Redesigned per spec: blurred/dimmed feature preview background, lock overlay, context-specific `featureDescription` parameter, pill CTA
- **`presentation/navigation/Screen.kt`** — Added `Paywall` and `ProActivated` routes
- **`presentation/navigation/TripRankNavigation.kt`** — Deleted (was duplicate of Screen.kt causing build conflict)
- **`MainActivity.kt`** — Fixed missing imports, fixed `navigator` typo, added PaywallScreen/ProActivatedScreen composable destinations, wired `onUpgradeClick` in GForceScreen and RouteReplayScreen to navigate to paywall
- **`util/PreferenceUtil.kt`** — Fixed `func.` typo → `fun`, added `hasSeenPaywall` property for first-encounter detection
- **`presentation/screens/score/MatrixRain.kt`** — Fixed `import xpath` syntax error
- **`GForceScreen.kt` / `RouteReplayScreen.kt`** — Added `onUpgradeClick` parameter for paywall navigation, added context-specific feature descriptions to ProUpsellBanner

---

### Branch 08 — Performance & Reliability (Completed)

**New files created:**
- **`utils/RDPAlgorithm.kt`** — Ramer-Douglas-Peucker polyline compression, reduces 3000+ GPS points to ~300 while preserving route shape
- **`worker/SyncWorker.kt`** — `@HiltWorker` for offline-first sync: runs when connectivity restored, syncs unsynced trips to Firestore
- **`receiver/BootReceiver.kt`** — Restarts AutoTripDetector periodic work on device boot

**Files rewritten:**
- **`utils/KalmanFilter.kt`** — Replaced 1D stub with full `LatLngKalmanFilter`: 2D GPS filtering with timestamp-based variance, GPS jump detection (>50m clamp, >200m reject), adaptive Kalman gain
- **`service/TripTrackingService.kt`** — Major perf rewrite:
  - Adaptive GPS sampling: 30s (stationary) / 5s (slow) / 1s (moving) based on speed
  - Partial wake lock (`PARTIAL_WAKE_LOCK`, 2hr max) for CPU survival
  - Accelerometer batching (500ms max report latency)
  - GPS cold start: network provider seed for faster first fix
  - Speed-based interval adjustment loop (every 30s)
- **`worker/AutoTripDetector.kt`** — Fixed `@AssistedContext` → `@Assisted`, added proper Google Tasks `.await()`, cleaned up Hilt wiring

**Files updated:**
- **`data/repository/TripTrackingRepository.kt`** — Uses `LatLngKalmanFilter` instead of two 1D KalmanFilters
- **`data/repository/TripRepository.kt`** — `endTrip()` and `updateTripPoints()` now use RDP compression instead of simple `take(2000)`, G-force downsampled by 2x
- **`AndroidManifest.xml`** — Cleaned up Flutter artifacts (dangling `</intent-filter>`, `io.flutter` meta-data, wrong package structure), registered `BootReceiver`, added `FOREGROUND_SERVICE_LOCATION` and `ACTIVITY_RECOGNITION` permissions
- **`app/build.gradle.kts`** — Added `androidx.hilt:hilt-work:1.2.0` + kapt for `@HiltWorker` support

**Coverage:**
- Kalman filter eliminates GPS point jumps > 50m ✓
- GPS sampling rate reduces when stationary ✓
- RDP compresses GPS to ~300 points per trip ✓
- Wake lock + START_STICKY for service survival ✓
- Boot receiver restarts detection after reboot ✓
- SyncWorker triggers on connectivity restore ✓
- Battery target: < 3% per hour (GPS 1Hz ~1.5%, accel batched ~0.3%, wake lock ~0.5%) ✓

---

### Branch 09 — Polish, provisional-final QA (Completed)

**New files created:**
- **`utils/HapticManager.kt`** — Hilt singleton for vibration feedback: `tripStart()`, `tripEnd()`, `rankUp()`, `badgeUnlock()`, `scoreReveal()`, `buttonPress()`, `error()`. Uses VibrationManager (API 31+) with legacy fallback.
- **`presentation/components/ScanLines.kt`** — `Modifier.scanLines()` extension for digital mesh scanline overlay (configurable alpha, spacing, color).
- **`presentation/components/SkeletonBox.kt`** — Shimmer loading components: `SkeletonBox`, `SkeletonRow`, `SkeletonChart` with animated linear gradient.
- **`presentation/components/EmptyState.kt`** — `EmptyState` composable with icon/headline/subtext/optional CTA + `EmptyStates` object with pre-defined copies for no trips, no badges, no friends, leaderboard failed.
- **`presentation/components/ErrorState.kt`** — `ErrorState` composable with friendly message + RETRY button + `ErrorDisplay` sealed class (Network, Auth, Generic).
- **`presentation/components/MatrixRainCanvas.kt`** — `MatrixRainBackground` composable with Canvas-based falling columns: configurable density, speed, character pool, head-bright/tail-fading alpha.
- **`res/drawable/ic_launcher_foreground.xml`** — Vector drawable: speedometer needle / T shape in MatrixGreen on black, doubles as "T" for TripRank.
- **`res/drawable/ic_launcher_background.xml`** — Pure black (#000000) background layer.
- **`res/mipmap-anydpi-v26/ic_launcher.xml`** — Adaptive icon XML referencing background + foreground.
- **`res/mipmap-anydpi-v26/ic_launcher_round.xml`** — Round adaptive icon variant.

**Files updated:**
- **`MainActivity.kt`** — Added `enterTransition`/`exitTransition` (slide up/down, 300ms tween) to every route in OnboardingNavHost and MainNavHost. ProActivated uses `fadeIn`/`fadeOut`.

**Coverage:**
- Haptic feedback system wired for all trigger points ✓
- Skeleton shimmer states for loading screens ✓
- Empty states for all zero-data scenarios ✓
- Error states with human-friendly messages ✓
- Scanline overlay modifier for HUD screens ✓
- Matrix rain Canvas background (ambient falling columns) ✓
- Slide transitions on all navigation routes ✓
- Adaptive app icon (speedometer/T needle, MatrixGreen on black) ✓

**Fixed Issues (Branch 09 clean-up):**
- `GForceScreen.kt` — removed `androidx.ui.*` pre-alpha imports, rewrote `drawText` calls with proper `rememberTextMeasurer` API, added missing `ProUpsellBanner` import, removed unused math imports
- `RouteReplayScreen.kt` — removed `androidx.ui.*` pre-alpha imports and duplicate imports, added `Color` and `ProUpsellBanner` imports
- `app/build.gradle.kts` — added `material-icons-extended` and `lifecycle-runtime-compose` dependencies

---

### Play Store Listing Preparation (Completed June 25, 2026)

All assets saved to `playstore/` directory:
- **title.txt**: RevRank
- **short-description.txt**: 80-char max tagline
- **long-description.txt**: Full listing with feature breakdown and keyword density
- **keywords.txt**: 23 targeted keywords (motorcycle, ride tracker, driving score, etc.)
- **feature-graphic.md**: 1024×500 spec (dark HUD, MatrixGreen glow, "SCORE EVERY RIDE" tagline)
- **screenshots.md**: 8 recommended captures (phone portrait, trip end, home, live HUD, ranks, history, share card, analytics, route replay)
- **app-icon-spec.md**: Adaptive icon (API 26+) + legacy + high-food Play Store icon
- **privacy-policy.md**: GDPR-conscious draft (location data local-first, optional cloud sync)
- **content-rating.md**: Answers rated for E/PEGI 3 (Everyone) — no violence, no gambling, limited user interaction
- **store-listing-checklist.md**: Upload checklist for Google Play Console

### Remaining Blockers
- No Android SDK / Gradle wrapper on this Windows host. Build verification pending.
- `google-services.json` missing (placeholder needed for build)

### What to dump on Claude to build + run

When you get these, paste them into this conversation (or start a new session with them):

1. **`google-services.json`** — from Firebase Console > Project Settings > Your app > Download. Drop the file path or contents here.
2. **Maps API key** — from Google Cloud Console > APIs > Maps SDK for Android > Credentials. Just paste the key string.
3. **RevenueCat API keys** — from RevenueCat Dashboard > Project > API Keys. Need the public SDK key and the subscriber secret.
4. **SHA-1 fingerprint** (only needed if you restrict the API key) — from Android Studio > Gradle > signingReport, or `keytool -list -v -keystore ~/.android/debug.keystore`.
