# 03 — Feature Roadmap

Each feature maps to a branch. Branches are executed sequentially unless marked PARALLEL.

---

## BRANCH 00 — Foundation
**Goal:** Clean project structure, auth that works, navigation shell
**Estimated time:** 3–4 days

### Features:
- [ ] **Google Sign-In** via Firebase Auth (kills the username loss bug)
- [ ] **Apple Sign-In** (prepare for iOS, add to Android as alternative)
- [ ] **Username claim system** — unique username stored in Firestore, recoverable on reinstall
- [ ] **Navigation shell** — bottom nav with 4 tabs: Home, Trips, Ranks, Profile
- [ ] **Room database schema** — Trip, User, Badge, Streak tables
- [ ] **Firestore sync layer** — background sync of trips to cloud when online
- [ ] **Hilt DI setup** — all modules wired
- [ ] **Design system tokens** — colors, typography, spacing as Compose theme
- [ ] **Firebase project setup** — Auth, Firestore, Storage, Analytics, Crashlytics

### Acceptance Criteria:
- User can sign in with Google
- Username persists across reinstalls
- Bottom nav navigates between 4 empty placeholder screens
- App doesn't crash on launch

---

## BRANCH 01 — Onboarding
**Goal:** First-time experience that sets expectations and excites
**Estimated time:** 2–3 days

### Features:
- [ ] **3-screen onboarding carousel**
  - Screen 1: "Your drives, ranked" — animated speedometer, rank badge
  - Screen 2: "Every trip scored" — sample trip score card with breakdown
  - Screen 3: "Free forever, unlock more" — clear free vs Pro list
- [ ] **Username picker** — with real-time availability check
- [ ] **Vehicle type selector** — Car / Motorcycle / Van / Truck (affects scoring weights)
- [ ] **Permission flow** — Location (always), Motion sensors — explained in plain English with *why* each is needed
- [ ] **Skip to app** option on every screen (non-blocking)
- [ ] **Onboarding completion flag** — never shown again after first completion

### Acceptance Criteria:
- New user completes onboarding in under 90 seconds
- Permission grant rate >75% (plain-English explanations help)
- User lands on Home with their username displayed

---

## BRANCH 02 — Trip Tracking (Core Rework)
**Goal:** Auto-detect trips, redesigned live HUD
**Estimated time:** 5–6 days

### Features:
- [ ] **Auto-trip detection**
  - Motion sensor detects vehicle movement (>5 km/h for 30 seconds = trip starts)
  - Foreground service with persistent notification
  - Auto-end when stationary for 3 minutes
  - User can override: force-start, force-end
- [ ] **Live HUD screen** (redesigned — see wireframes)
  - Large central speedometer (analog needle style, matrix green glow)
  - Current trip score (live updating)
  - Distance ticker
  - Trip time
  - Smooth/aggressive indicator (green/amber/red glow on border)
  - Minimal UI — designed to be glanced at while in a mount
- [ ] **Background tracking** — trip continues when app is backgrounded or screen off
- [ ] **GPS accuracy improvement**
  - Kalman filter on GPS points to smooth jitter
  - Fallback to last known location during signal loss
  - Signal strength indicator on HUD
- [ ] **Sensor fusion**
  - Combine GPS speed + accelerometer for accuracy
  - Detect hard braking (>0.4g deceleration)
  - Detect rapid acceleration (>0.35g)
  - Detect sharp corners (lateral g >0.4g)

### Acceptance Criteria:
- App detects driving start within 45 seconds of moving
- No missed trip ends (auto-end works reliably)
- GPS speed within 3 km/h of actual speed
- HUD readable at a glance

---

## BRANCH 03 — Trip Summary (Cinematic End Screen)
**Goal:** The payoff moment — make every trip ending feel rewarding
**Estimated time:** 3–4 days

### Features:
- [ ] **Score reveal animation**
  - Score counts up from 0 to final value over 1.5 seconds
  - Grade letter appears (S/A/B/C/D) with glow effect
  - Matrix-style scanline wipe reveals stats one by one
- [ ] **Score breakdown card**
  - 5 category bars: Acceleration, Braking, Cornering, Smoothness, Consistency
  - Each bar animates in sequentially
  - Comparison to user's personal average ("↑ 8 pts above your average")
- [ ] **Trip stats row** — Max speed, Distance, Duration, Stops
- [ ] **Badge earned notification** — if trip earned a new badge, full-screen badge pop
- [ ] **XP gained ticker** — "+45 XP" floats up with particle effect
- [ ] **Route map thumbnail** — mini map showing the driven route (color coded: green=smooth, red=aggressive moments)
- [ ] **Shareable card generator**
  - Canvas-rendered image: username, score, grade, key stats, rank badge
  - Matrix aesthetic with glowing border
  - Share sheet opens natively
- [ ] **"Compare to last trip on this route"** — if route pattern matches a previous trip

### Acceptance Criteria:
- End screen loads within 2 seconds of trip ending
- Share card renders correctly at 1080×1080px
- Animation sequence completes in under 4 seconds

---

## BRANCH 04 — Gamification System
**Goal:** Ranks, XP, badges, streaks — the retention engine
**Estimated time:** 4–5 days

### Features:

#### Rank System (7 Tiers)
| Rank | Name | XP Required | Icon |
|------|------|-------------|------|
| 1 | Learner | 0 | Grey L-plate |
| 2 | Cruiser | 500 | Green diamond |
| 3 | Road Captain | 1500 | Blue chevron |
| 4 | Apex Driver | 3500 | Purple crown |
| 5 | Street Ghost | 7000 | Cyan ghost |
| 6 | Midnight Racer | 15000 | Red/gold badge |
| 7 | Legend | 30000 | Animated gold |

- [ ] **XP calculation**: Base XP = trip score × distance multiplier. Bonus XP for streaks, perfect scores, first trips on new routes
- [ ] **Rank-up screen** — full-screen celebration with Lottie animation, new rank badge reveal
- [ ] **Rank progress bar** on Home screen — shows XP to next rank

#### Badge System (30 badges across 5 categories)
**Distance badges:**
- First Drive (1 trip)
- 100km Club
- 1000km Veteran
- Night Rider (10 trips after 10 PM)
- Weekend Warrior (5 weekend trips)

**Score badges:**
- Perfect Run (score 100)
- Consistent (5 trips all above 80)
- Comeback Kid (improve score by 20+ vs previous trip)
- The Surgeon (braking score 100 on 3 trips)
- Smooth Criminal (cornering score 100 on 3 trips)

**Streak badges:**
- On A Roll (3-day streak)
- Week Warrior (7-day streak)
- Iron Driver (30-day streak)

**Speed badges:**
- Highway Star (max speed 120+ km/h — legal context noted)
- Slow and Steady (avg speed under 40 km/h, score above 90)

**Social badges:**
- First Share
- Challenge Accepted (complete a friend challenge)
- Trendsetter (5 people follow your shared card)

- [ ] **Badge display screen** — grid of all badges, locked badges shown greyed out with hint text
- [ ] **Badge detail modal** — tap badge to see name, description, how to unlock, date earned

#### Streak System
- [ ] **Daily streak counter** — drive at least once per day
- [ ] **Streak display** on Home (flame icon + count)
- [ ] **Streak freeze** — one free freeze per week (Pro: unlimited freezes)
- [ ] **Streak lost notification** — "Your 7-day streak ends tonight. Take a quick drive!"

#### Challenges (Weekly)
- [ ] **3 weekly challenges** auto-generated based on user's weak areas
  - "Complete 3 trips with braking score above 75"
  - "Drive 50km this week"  
  - "Beat your personal best score"
- [ ] **Challenge progress bar** visible on Home
- [ ] **Challenge completion XP bonus** (+100 XP per challenge)

### Acceptance Criteria:
- XP and rank persist across sessions
- Badges unlock correctly based on criteria
- Rank-up animation plays exactly once per rank-up

---

## BRANCH 05 — Social Layer
**Goal:** Virality through sharing and competition
**Estimated time:** 3–4 days

### Features:
- [ ] **Redesigned share cards** (matrix aesthetic)
  - Trip card: Score grade, key stats, rank badge, username, TripRank branding
  - Weekly recap card: Week summary, best trip, total distance, rank progress
  - Badge card: Badge earned, username, "Challenge me" CTA
- [ ] **Deep link system** — every share card has a link to the app with the trip context
- [ ] **Friend challenges**
  - "Beat my score on [route]" — generates a challenge link
  - Recipient opens link, app opens (or prompts install), challenge stored
  - When challenger drives same route, scores compared
  - Push notification to challenger: "Someone accepted your challenge!"
- [ ] **Global weekly leaderboard** (free — top 100 scores this week)
- [ ] **Friends leaderboard** (Pro — weekly scores among friends)
- [ ] **Profile page** — public-facing, shows rank, badges, recent trips, username
- [ ] **"Rival" system** — app auto-suggests a rival (user with similar XP) to chase

### Acceptance Criteria:
- Share card renders and shares within 3 seconds
- Deep links open correct context in app
- Leaderboard updates within 1 hour of trip completion

---

## BRANCH 06 — Pro Features
**Goal:** Features that justify $3.99/mo
**Estimated time:** 5–6 days

### Features:
- [ ] **Route replay** — play back GPS track on map, scrub through timeline
  - Color gradient on route line: green (smooth) → yellow → red (aggressive moments)
  - Speed chart synced to map scrub position
  - Markers for hard braking, acceleration, cornering events
- [ ] **G-force visualizer** — radial chart showing lateral/longitudinal g-forces during trip
- [ ] **Advanced analytics dashboard**
  - Score trend chart (30/90 day)
  - Best time of day to drive (avg score by hour)
  - Best/worst categories radar chart
  - Monthly distance heatmap (calendar view)
- [ ] **Unlimited trip history** (free users see last 10 only)
- [ ] **CSV export** — all trips exported as spreadsheet
- [ ] **Custom route naming** — name a route, future trips on same path auto-tagged
- [ ] **Home screen widget** — live speedometer + current trip stats
- [ ] **Streak freeze** — unlimited (free users get 1/week)

### Acceptance Criteria:
- Route replay smooth at 60fps
- Widget updates within 5 seconds of speed change
- Analytics charts render with real data

---

## BRANCH 07 — Monetization UX
**Goal:** Paywall that converts without alienating
**Estimated time:** 2 days

### Features:
- [ ] **Paywall redesign** — no full-screen hard block; soft upsell approach
  - "You've unlocked Pro features" moment — show route replay thumbnail blurred
  - "Upgrade to see this" inline prompt (not full-screen takeover)
  - Full paywall only on explicit "Get Pro" tap
- [ ] **Paywall screen** — matrix aesthetic, animated feature list, monthly/annual toggle
- [ ] **Free trial** — 7-day free Pro trial on first paywall encounter
- [ ] **Referral system** — "Give a friend 1 month Pro, you get 1 month Pro" via share link
- [ ] **Restore purchases** button (Play Store requirement)
- [ ] **RevenueCat integration** — subscription management, webhook to Firestore
- [ ] **Entitlement check** on app foreground — graceful degradation if Pro lapses

### Acceptance Criteria:
- No user hits a hard paywall before completing 3 trips
- Free trial activates correctly
- Lapsed Pro users lose Pro features gracefully (no crash, no data loss)

---

## BRANCH 08 — Performance
**Goal:** Fix GPS lag, battery drain, background reliability
**Estimated time:** 3–4 days

### Features:
- [ ] **Kalman filter** on GPS coordinates — smooth GPS jitter, eliminate teleporting
- [ ] **Adaptive GPS sampling** — 1 Hz while stopped, 5 Hz while moving (battery saving)
- [ ] **Background trip survival** — foreground service properly handles Doze mode
- [ ] **Battery optimisation whitelist prompt** — ask user to exclude app from battery optimisation (with explanation)
- [ ] **GPS cold start reduction** — use network location as seed for faster GPS lock
- [ ] **Sensor batching** — batch accelerometer reads for efficiency
- [ ] **Trip data compression** — compress GPS polylines for storage (Encoded Polyline Algorithm)
- [ ] **Offline-first** — all trips saved locally first, sync to cloud when on WiFi/charging

### Acceptance Criteria:
- GPS locks within 10 seconds of trip start
- Battery impact <3% per hour of tracking
- 0 missed trips due to background kill

---

## BRANCH 09 — Polish
**Goal:** Animations, haptics, micro-interactions, final QA
**Estimated time:** 2–3 days

### Features:
- [ ] **Haptic feedback** — rank up, badge unlock, trip start/end, score reveal
- [ ] **Loading states** — skeleton screens everywhere (no spinners)
- [ ] **Empty states** — illustrated, with CTA (no blank screens)
- [ ] **Error states** — clear messages, retry buttons (no generic "Something went wrong")
- [ ] **Transition animations** — shared element transitions between trip card → trip detail
- [ ] **Score counter animation** — satisfying number roll-up
- [ ] **Rank progress bar animation** — XP fills in smoothly
- [ ] **Lottie animations** — rank-up, badge unlock, streak start
- [ ] **Dark mode only** (the aesthetic demands it — no light mode)
- [ ] **Accessibility** — minimum 4.5:1 contrast on all text, touch targets 48dp minimum
- [ ] **Play Store screenshots** — new screenshots showcasing matrix aesthetic
- [ ] **App icon update** — matrix-themed, distinctive
