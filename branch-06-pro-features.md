# Branch 06 — Pro Features

## Prerequisites
Branch 05 merged. RevenueCat integrated (see Branch 07 for paywall UX — add entitlement checks here but not the paywall screens themselves).

## Entitlement Check Pattern
```kotlin
// Use everywhere a Pro feature is gated:
@Composable
fun ProGate(content: @Composable () -> Unit, fallback: @Composable () -> Unit = { ProUpsellBanner() }) {
    val isPro by LocalProStatus.current.collectAsState()
    if (isPro) content() else fallback()
}
```

---

## Route Replay (Pro)

### RouteReplayScreen.kt
Nav arg: `tripId: String`

Data needed: GPS points list (stored in Room as JSON blob, max 2000 points per trip — trim to 2000 using Ramer-Douglas-Peucker algorithm during save).

Layout:
```
Full screen Google Map (or Mapbox — see note)
  - Dark map style (night mode JSON for Google Maps)
  - Polyline drawn with color gradient:
      score 90–100 segment = MatrixGreen
      score 70–89  segment = #39FF14
      score 50–69  segment = #FFD700
      score 30–49  segment = #FF6B00
      score 0–29   segment = #FF2D00
  - Event markers: brake icon (red), accel icon (amber), corner icon (blue)

BOTTOM SHEET (partial, draggable):
  Playback controls:
    [◄◄] [▶/❚❚] [►► 2x]
  Scrub bar: current position in trip
  Speed chart (Vico or MPAndroidChart): synced to scrub position
    X axis = time, Y axis = speed km/h
    Current position indicator line on chart moves with scrub
  Current segment stats: speed at cursor, score at cursor
```

**Segment scoring:** During trip save, divide GPS polyline into 10-second segments. Score each segment using the sensor data from that window. Store as `List<TripSegment>` alongside trip.

**Map choice:** Use Google Maps SDK (already a dependency via play-services). Night mode style JSON: use Snazzy Maps "Assassin" style or similar dark retro style.

---

## G-Force Visualizer (Pro)

### GForceScreen.kt (tab in TripDetailScreen)

Radial chart showing lateral (X) vs longitudinal (Y) g-forces over the whole trip.

```
Circular grid (like a radar target):
  Rings at 0.2g, 0.4g, 0.6g increments
  Crosshairs (front/back/left/right labeled)
  Each data point: small dot
    Color = score color at that moment
    Opacity 60% to handle overlap
  "Safe zone" circle at 0.3g radius — subtle green shading inside
  Points outside 0.3g = aggressive zone

Bottom: summary stats
  Max lateral G: {n}g
  Max longitudinal G: {n}g
  Time in safe zone: {n}%
```

Use Compose Canvas for the entire chart. No library needed — it's a scatter plot in polar space.

Store sensor data: during trip, downsample to 1 reading per 500ms and store in Room as `List<GForcePoint>` (max 1000 points per trip).

---

## Advanced Analytics (Pro)

### AnalyticsScreen.kt

Tabs: [SCORES] [DISTANCE] [CATEGORIES]

**Scores tab:**
- Line chart: last 30 trips, X=date, Y=score
- Use MPAndroidChart or Vico library
- Dark theme, MatrixGreen line, grid lines in Border color
- Moving average overlay (7-trip rolling avg) in amber
- Tap a point → shows trip summary tooltip

**Distance tab:**
- Calendar heatmap: current month + 2 previous
- Each day = small square, color intensity = distance driven
- No drives = Surface2, drives = gradient from MatrixMuted to MatrixGreen
- Tap day → shows trips that day

**Categories tab:**
- Radar chart: 5 axes (acceleration/braking/cornering/smoothness/consistency)
- Shows: [This month avg] vs [All time avg] — two overlapping polygons
- MatrixGreen polygon (current) vs dim white polygon (all time)
- Caption below each axis showing the score number

All charts use real data from Room DB — queries via AnalyticsViewModel.

---

## CSV Export (Pro)

```kotlin
// ExportManager.kt
suspend fun exportTripsCSV(trips: List<Trip>): Uri {
    val header = "date,distance_km,duration_min,max_speed,avg_speed,score,grade,acceleration,braking,cornering,smoothness,consistency"
    val rows = trips.map { trip ->
        "${trip.startTime.toDateString()},${trip.distanceKm},${trip.durationMinutes}..."
    }
    // Write to cache dir, return FileProvider Uri
    // User can share to Google Drive, email, etc.
}
```

In ProfileScreen → Settings: "Export My Data (CSV)" — Pro gated.

---

## Home Screen Widget (Pro)

Create `TripRankWidget.kt` using Glance API (Jetpack Glance for Compose-based widgets):

Size: 2×1 cells minimum (medium widget)

Content:
```
[TripRank logo small]  [Rank badge]
[  128  ]  KM/H        [TRACKING ●]  if tracking
[or last trip score if idle]
```

Update frequency: 
- During trip: every 5 seconds via `GlanceAppWidgetManager.update()`
- Idle: update after each trip ends

Requires: `BIND_APPWIDGET` permission, `AppWidgetProvider` in manifest.

---

## Unlimited Trip History (Pro)

In TripsScreen.kt:
- Free: show trips from last 30 days only (max 10 visible), soft gate below
- Pro: all trips, infinite scroll, group by month

The query difference:
```kotlin
// Free:
dao.getTrips(userId, limit = 10, since = thirtyDaysAgo)
// Pro: 
dao.getAllTrips(userId)  // paginated with Paging 3
```

---

## Acceptance Criteria
- [ ] Route replay shows colored polyline synced to trip segments
- [ ] Map scrub moves speed chart cursor in sync
- [ ] G-force chart renders all data points within 1 second
- [ ] Analytics line chart loads 30 trips correctly
- [ ] Calendar heatmap shows correct color intensity per day
- [ ] CSV exports all trips with correct data
- [ ] Widget shows live speed during trip, score after
- [ ] All Pro features show `ProUpsellBanner` for free users (not crash, not hide)
- [ ] Entitlement check survives Pro lapse gracefully
