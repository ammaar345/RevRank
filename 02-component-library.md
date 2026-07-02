# 02 — Component Library

Every component listed here must be built in `presentation/components/` and imported wherever needed. Build these in Branch 00 alongside the theme.

---

## TripRankButton
```kotlin
@Composable
fun TripRankButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.OUTLINED,  // OUTLINED | FILLED | TEXT
    enabled: Boolean = true
)
// OUTLINED: transparent bg, 1dp MatrixGreen border, MatrixGreen text
// FILLED: MatrixGreen bg, Void text (use for primary CTA only)
// TEXT: no border, MatrixDim text, no bg
// ALL: 4dp corner radius, 48dp height, Rajdhani SemiBold 14sp ALL CAPS, 2sp letter-spacing
```

## ScoreBadge
```kotlin
@Composable
fun ScoreBadge(score: Int, size: BadgeSize = BadgeSize.MEDIUM)
// Shows score number + grade letter
// Border color = grade color
// SMALL: 40dp — for list items
// MEDIUM: 64dp — for cards  
// LARGE: 96dp — for trip end screen (but use animation wrapper there)
```

## RankBadge (from Branch 04 spec)

## ScoreCategoryBar
```kotlin
@Composable
fun ScoreCategoryBar(label: String, score: Int, animate: Boolean = true)
// label: ALL CAPS, 10sp Share Tech Mono, TextSecondary
// score: right-aligned, 14sp Share Tech Mono, grade color
// bar: 4dp height, full width, gradient fill from dim to grade color, glow
// animate: animateFloatAsState width 0→score/100 on first composition
```

## TripCard
```kotlin
@Composable
fun TripCard(trip: Trip, onClick: () -> Unit, onShare: () -> Unit)
// Used in HomeScreen (last trip) and TripHistory list
// Surface card, 1dp border
// Row: date/route label | ScoreBadge SMALL | [View] [Share] buttons
```

## StatChip
```kotlin
@Composable
fun StatChip(value: String, label: String)
// Used in 2–3 column stat rows
// value: 20sp Share Tech Mono, TextPrimary
// label: 10sp Rajdhani SemiBold ALL CAPS, TextSecondary, letter-spacing 2sp
```

## SectionHeader
```kotlin
@Composable
fun SectionHeader(title: String, action: Pair<String, () -> Unit>? = null)
// title: 12sp Rajdhani SemiBold ALL CAPS, MatrixDim, letter-spacing 2sp
// optional right-aligned action link
// 1dp bottom border, MatrixMuted
```

## ProBadgeChip
```kotlin
@Composable
fun ProBadgeChip()
// Small amber pill: "PRO" in 10sp Rajdhani SemiBold
// Background: amber 15% alpha, border: 1dp amber
// Used inline next to Pro feature labels
```

## StreakIndicator
```kotlin
@Composable
fun StreakIndicator(days: Int, isActive: Boolean)
// 🔥 icon (or custom flame SVG) + "{n} day streak"
// Active: MatrixGreen flame, bright text
// Inactive/broken: grey flame, dim text
```

## ChallengeRow
```kotlin
@Composable
fun ChallengeRow(challenge: WeeklyChallenge)
// Title (14sp) + progress bar (4dp, animated) + "n/total" label right-aligned
// Completed: green checkmark, bar full, dim text (de-emphasised)
```

## MatrixDivider
```kotlin
@Composable
fun MatrixDivider()
// 1dp horizontal rule, MatrixMuted color
// With optional label: centered label on rule (used for section breaks)
```

## SkeletonBox (from Branch 09 spec, build in Branch 00)

## EmptyState (from Branch 09 spec)

## ErrorState (from Branch 09 spec)
