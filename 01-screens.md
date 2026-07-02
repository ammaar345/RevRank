# 01 — Screen Wireframes

All wireframes represent portrait phone (360×800dp equivalent).
`█` = filled/active  `░` = surface/card  `·` = empty space

---

## ONBOARDING — Screen 1 of 3

```
┌─────────────────────────────┐
│                             │
│                             │
│         [ANIMATED]          │
│      ┌───────────────┐      │
│      │  SPEEDOMETER  │      │
│      │   ◉ NEEDLE    │      │
│      │   128 KM/H    │      │
│      └───────────────┘      │
│                             │
│                             │
│    YOUR DRIVES, RANKED.     │  ← 28sp Share Tech Mono
│                             │
│  Every trip you take gets   │  ← 14sp Rajdhani
│  a score. Build your rank.  │
│  Become a legend.           │
│                             │
│                             │
│  ●  ○  ○                   │  ← progress dots
│                             │
│  ┌─────────────────────┐   │
│  │       NEXT →        │   │  ← primary button
│  └─────────────────────┘   │
│                             │
│         SKIP                │  ← text link, dim
└─────────────────────────────┘
```

---

## ONBOARDING — Screen 2 of 3

```
┌─────────────────────────────┐
│                             │
│  SAMPLE TRIP SCORE          │  ← small label
│  ┌─────────────────────┐   │
│  │  ░░░░░░░░░░░░░░░░░  │   │
│  │  82        A        │   │  ← big score + grade
│  │  ─────────────────  │   │
│  │  ACCELERATION  ████░│   │
│  │  BRAKING       ███░░│   │
│  │  CORNERING     █████│   │
│  │  SMOOTHNESS    ████░│   │
│  │  CONSISTENCY   ███░░│   │
│  │                     │   │
│  │  48km  37min  4stops│   │
│  └─────────────────────┘   │
│                             │
│  KNOW HOW YOU DRIVE.        │  ← 28sp headline
│                             │
│  See a real breakdown of    │
│  every trip. Acceleration,  │
│  braking, cornering, and    │
│  more. All free.            │
│                             │
│  ●  ●  ○                   │
│  ┌─────────────────────┐   │
│  │       NEXT →        │   │
│  └─────────────────────┘   │
│         SKIP                │
└─────────────────────────────┘
```

---

## ONBOARDING — Screen 3 of 3 (Free vs Pro)

```
┌─────────────────────────────┐
│                             │
│  FREE FOREVER               │  ← matrix green label
│  ┌─────────────────────┐   │
│  │ ✓  Trip tracking    │   │
│  │ ✓  Trip scores      │   │
│  │ ✓  Rank system      │   │
│  │ ✓  All badges       │   │
│  │ ✓  Weekly recap     │   │
│  └─────────────────────┘   │
│                             │
│  PRO UNLOCKS                │  ← amber label
│  ┌─────────────────────┐   │
│  │ ◈  Route replay     │   │
│  │ ◈  Full history     │   │
│  │ ◈  Friends board    │   │
│  │ ◈  Analytics        │   │
│  │ ◈  Home widget      │   │
│  └─────────────────────┘   │
│                             │
│  ●  ●  ●                   │
│  ┌─────────────────────┐   │
│  │   START DRIVING →   │   │  ← CTA
│  └─────────────────────┘   │
│    Try Pro free for 7 days  │  ← small text link
└─────────────────────────────┘
```

---

## HOME SCREEN

```
┌─────────────────────────────┐
│  TRIPRANK        [👤] [🔔]  │  ← top bar
├─────────────────────────────┤
│                             │
│  Hey, GhostRider_88         │  ← 16sp
│                             │
│  ┌─────────────────────┐   │
│  │  ROAD CAPTAIN       │   │  ← rank name, blue
│  │  ████████████░░░░░  │   │  ← XP bar
│  │  1,240 / 1,500 XP   │   │
│  │  260 XP to APEX     │   │
│  └─────────────────────┘   │
│                             │
│  ┌──────────┐ ┌──────────┐ │
│  │  🔥 7    │ │  THIS WK │ │
│  │  day     │ │  3 trips │ │
│  │  streak  │ │  147 km  │ │
│  └──────────┘ └──────────┘ │
│                             │
│  WEEKLY CHALLENGES          │  ← section header
│  ┌─────────────────────┐   │
│  │ ○ 3 trips >75 brake │   │
│  │   ██░░░░ 1/3        │   │
│  │ ○ Drive 50km        │   │
│  │   █████████░ 43/50  │   │
│  │ ○ Beat your PB      │   │
│  │   ─ Not started     │   │
│  └─────────────────────┘   │
│                             │
│  LAST TRIP                  │
│  ┌─────────────────────┐   │
│  │ Today · 14km · 22min│   │
│  │ 78    B    ↑+6 avg  │   │
│  │ [View] [Share]      │   │
│  └─────────────────────┘   │
│                             │
├─────────────────────────────┤
│  [🏠]  [📋]  [🏆]  [👤]   │  ← bottom nav
└─────────────────────────────┘
```

---

## LIVE HUD (During Trip)

```
┌─────────────────────────────┐
│  ●  TRACKING    00:14:32   │  ← red dot + timer, minimal
│─────────────────────────────│
│                             │
│                             │
│                             │
│          ┌───────┐          │
│          │  128  │          │  ← 96sp, Share Tech Mono
│          │ KM/H  │          │  ← 12sp below
│          └───────┘          │
│                             │
│        [GREEN GLOW]         │  ← border color = driving quality
│                             │
│  ┌──────────┬────────────┐  │
│  │  14.2 km │  SCORE: 84 │  │
│  └──────────┴────────────┘  │
│                             │
│                             │
│                             │
│                             │
│                             │
│                             │
│                             │
│  ┌─────────────────────┐   │
│  │      ■ END TRIP     │   │  ← large, bottom, easy to tap
│  └─────────────────────┘   │
└─────────────────────────────┘
```

Note: During trip, minimal UI. No bottom nav. Full screen for at-a-glance use in mount.
Border glow color changes: green (smooth) → amber (moderate) → red (aggressive)

---

## TRIP END — Score Reveal

```
┌─────────────────────────────┐
│                             │
│  [MATRIX RAIN ANIMATION]    │
│  [Characters resolve...]    │
│                             │
│         ┌─────────┐         │
│         │   87    │         │  ← 64sp, counts up
│         └─────────┘         │
│                             │
│              A              │  ← 64sp grade, drops in
│                             │
│  ─────────────────────────  │
│  ACCELERATION    ████████░  │
│  BRAKING         ███████░░  │
│  CORNERING       █████████  │
│  SMOOTHNESS      ████████░  │
│  CONSISTENCY     ███████░░  │
│                             │
│  ↑ 9 pts above your average │  ← small, green
│                             │
│  ─────────────────────────  │
│  22km     41min    3 stops  │
│  84 km/h  48 km/h max/avg   │
│                             │
│  [BADGE EARNED: SMOOTH RUN] │  ← if applicable, pops
│                             │
│  +62 XP ↑                  │  ← floats up, green
│                             │
│  ┌──────────┐ ┌──────────┐ │
│  │  SHARE   │ │  HISTORY │ │
│  └──────────┘ └──────────┘ │
└─────────────────────────────┘
```

---

## TRIP HISTORY

```
┌─────────────────────────────┐
│  TRIPS              [FILTER]│
├─────────────────────────────┤
│                             │
│  THIS WEEK  ·  3 trips      │
│  ┌─────────────────────┐   │
│  │ MON  22 Jun  14km   │   │
│  │ ████████████ 87  A  │   │
│  │─────────────────────│   │
│  │ SUN  21 Jun  8km    │   │
│  │ ██████████░░ 81  B  │   │
│  │─────────────────────│   │
│  │ SAT  20 Jun  31km   │   │
│  │ ████████████ 91  S  │   │
│  └─────────────────────┘   │
│                             │
│  LAST WEEK  ·  4 trips      │
│  ┌─────────────────────┐   │
│  │ [collapsed — tap]   │   │
│  └─────────────────────┘   │
│                             │
│  ┌─────────────────────┐   │
│  │  🔒 Older trips...  │   │  ← free tier limit prompt
│  │  Upgrade to Pro to  │   │
│  │  unlock full history│   │
│  └─────────────────────┘   │
│                             │
├─────────────────────────────┤
│  [🏠]  [📋]  [🏆]  [👤]   │
└─────────────────────────────┘
```

---

## RANK SCREEN (Gamification Hub)

```
┌─────────────────────────────┐
│  RANKS & BADGES             │
├─────────────────────────────┤
│                             │
│  YOUR RANK                  │
│  ┌─────────────────────┐   │
│  │  [CHEVRON ICON]     │   │
│  │  ROAD CAPTAIN       │   │  ← rank name, blue
│  │  Rank 3 of 7        │   │
│  │  ████████████░░░░░  │   │
│  │  1,240 / 1,500 XP   │   │
│  └─────────────────────┘   │
│                             │
│  RANK LADDER                │
│  ┌─────────────────────┐   │
│  │ ✓ LEARNER           │   │  ← completed, dim
│  │ ✓ CRUISER           │   │
│  │ ● ROAD CAPTAIN      │   │  ← current, blue glow
│  │ · APEX DRIVER       │   │  ← locked, dim
│  │ · STREET GHOST      │   │
│  │ · MIDNIGHT RACER    │   │
│  │ · LEGEND            │   │
│  └─────────────────────┘   │
│                             │
│  YOUR BADGES  (12/30)       │
│  ┌─┐ ┌─┐ ┌─┐ ┌─┐ ┌─┐     │
│  │█│ │█│ │█│ │░│ │░│     │  ← 5 across, earned vs locked
│  └─┘ └─┘ └─┘ └─┘ └─┘     │
│  ┌─┐ ┌─┐ ┌─┐ ┌─┐ ┌─┐     │
│  │█│ │░│ │░│ │░│ │░│     │
│  └─┘ └─┘ └─┘ └─┘ └─┘     │
│                             │
│  [VIEW ALL BADGES]          │
│                             │
├─────────────────────────────┤
│  [🏠]  [📋]  [🏆]  [👤]   │
└─────────────────────────────┘
```

---

## LEADERBOARD

```
┌─────────────────────────────┐
│  LEADERBOARD   [Global][You]│
├─────────────────────────────┤
│                             │
│  THIS WEEK · JUN 16–22      │
│                             │
│  ┌─────────────────────┐   │
│  │ 1  NightShift_K     │   │
│  │    [LEGEND]  94 avg │   │
│  │─────────────────────│   │
│  │ 2  Throttle99       │   │
│  │    [MIDNIGHT] 91avg │   │
│  │─────────────────────│   │
│  │ 3  VelvetDrift      │   │
│  │    [APEX]   89 avg  │   │
│  │─────────────────────│   │
│  │ ...                 │   │
│  │─────────────────────│   │
│  │ 47 ► GhostRider_88  │   │  ← YOUR position, highlighted
│  │    [CAPTAIN] 81avg  │   │
│  └─────────────────────┘   │
│                             │
│  FRIENDS LEADERBOARD        │
│  ┌─────────────────────┐   │
│  │ 🔒 Upgrade to Pro   │   │
│  │ to see how you rank │   │
│  │ against friends     │   │
│  └─────────────────────┘   │
│                             │
├─────────────────────────────┤
│  [🏠]  [📋]  [🏆]  [👤]   │
└─────────────────────────────┘
```

---

## PROFILE SCREEN

```
┌─────────────────────────────┐
│  PROFILE           [EDIT]   │
├─────────────────────────────┤
│                             │
│  ┌────┐                     │
│  │ GR │  GhostRider_88      │  ← avatar + username
│  └────┘  ROAD CAPTAIN       │  ← rank in rank color
│          1,240 XP           │
│                             │
│  ─────────────────────────  │
│  87    |  214km  |  26 trips│  ← avg score | distance | trips
│  avg   |  total  |  total   │
│  ─────────────────────────  │
│                             │
│  BADGES (12)                │
│  ┌─┐ ┌─┐ ┌─┐ ┌─┐  [ALL →] │
│  │█│ │█│ │█│ │█│           │
│  └─┘ └─┘ └─┘ └─┘           │
│                             │
│  STREAK         🔥 7 days   │
│                             │
│  ─────────────────────────  │
│  [SHARE PROFILE]            │
│  [INVITE FRIENDS]           │
│  [UPGRADE TO PRO]  ← amber  │
│  ─────────────────────────  │
│  [SETTINGS]                 │
│  [HELP & FEEDBACK]          │
│  [SIGN OUT]                 │
│                             │
├─────────────────────────────┤
│  [🏠]  [📋]  [🏆]  [👤]   │
└─────────────────────────────┘
```

---

## PRO PAYWALL (Soft — Inline)

```
┌─────────────────────────────┐
│  TRIP DETAIL                │
├─────────────────────────────┤
│  [score, stats, breakdown]  │
│  ...                        │
│                             │
│  ROUTE REPLAY               │
│  ┌─────────────────────┐   │
│  │  ░░░░░░ MAP BLUR ░░ │   │  ← blurred route map
│  │  ░░░░░░░░░░░░░░░░░░ │   │
│  │  ┌─────────────────┐│   │
│  │  │ 🔒 PRO FEATURE  ││   │
│  │  │ See your full   ││   │
│  │  │ route with score││   │
│  │  │ overlay         ││   │
│  │  │ [UNLOCK PRO]    ││   │  ← amber button
│  │  └─────────────────┘│   │
│  └─────────────────────┘   │
│                             │
└─────────────────────────────┘
```

---

## SHAREABLE TRIP CARD (1080×1080px rendered)

```
┌─────────────────────────────┐
│ ░░░░░░░░░░░░░░░░░░░░░░░░░░ │  ← scan line texture
│                             │
│  TRIPRANK          [LOGO]   │  ← small wordmark top right
│                             │
│  GhostRider_88              │  ← username
│  ROAD CAPTAIN  [chevron]    │  ← rank badge
│                             │
│  ────────────────────────   │
│                             │
│         87                  │  ← huge score, matrix green
│          A                  │  ← grade, glowing
│                             │
│  ────────────────────────   │
│                             │
│  22km    41min   84 max     │
│  DIST    TIME    KM/H       │
│                             │
│  ████████████████░░  BRAKE  │
│  ███████████████░░░  ACCEL  │
│  █████████████████░  CORNER │
│                             │
│  ────────────────────────   │
│  triprank.app               │  ← call to action
└─────────────────────────────┘
```
