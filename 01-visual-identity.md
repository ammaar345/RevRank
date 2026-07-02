# 01 — Visual Identity: Matrix/Retro Design System

## Design Direction: "Ghost in the Machine"

TripRank's redesign takes its visual cues from three sources:
1. **The Matrix (1999)** — digital rain, phosphor green on black, scan lines, terminal typography
2. **1980s Rally Dashboards** — analog gauges, amber warning lights, segmented displays
3. **Arcade Racing Games (OutRun, Initial D)** — neon speed lines, score pop-ups, rank reveals

The result: a UI that feels like you're inside a performance computer reading your car's soul. Dark. Fast. Data-dense but instantly readable.

**The single signature element:** Every trip score reveal uses a **cascading green character rain** (Matrix-style) that resolves into your score number. It takes 1.5 seconds and is unforgettable.

---

## Color Tokens

```
// Backgrounds
--color-void:        #000000   // pure black — app background
--color-surface:     #0A0A0A   // card surfaces
--color-surface-2:   #111111   // elevated surfaces, modals
--color-surface-3:   #1A1A1A   // inputs, secondary cards

// Matrix Green Family (primary brand)
--color-matrix:      #00FF41   // THE green — used sparingly for hero moments
--color-matrix-dim:  #00C032   // body text on dark, active states
--color-matrix-muted:#004D14   // borders, dividers, inactive states
--color-matrix-glow: rgba(0,255,65,0.15) // ambient glow backgrounds

// Speed / Score Colors (semantic)
--color-perfect:     #00FF41   // score 90–100, S grade (matrix green)
--color-great:       #39FF14   // score 75–89, A grade (neon green)
--color-good:        #FFD700   // score 55–74, B grade (amber)
--color-okay:        #FF6B00   // score 35–54, C grade (orange)
--color-poor:        #FF2D00   // score 0–34, D grade (red)

// Rank Colors
--rank-learner:      #888888   // grey
--rank-cruiser:      #00C032   // green
--rank-captain:      #0099FF   // blue
--rank-apex:         #9B59B6   // purple
--rank-ghost:        #00FFFF   // cyan
--rank-midnight:     #FF4444   // red-gold
--rank-legend:       #FFD700   // animated gold

// UI Neutrals
--color-text-primary:   #E8E8E8
--color-text-secondary: #888888
--color-text-dim:       #444444
--color-border:         #1E1E1E
--color-border-glow:    #003010

// Alert Colors
--color-danger:  #FF2D00
--color-warning: #FF8C00
--color-success: #00FF41
--color-info:    #0099FF
```

---

## Typography

### Display Face: **Share Tech Mono** (Google Fonts — Free)
Used for: scores, speeds, numbers, rank names, grade letters
- Feel: Terminal, digital, data-native
- Weight: Regular (400) — the monospace geometry does the work
- Usage: Large numbers, score counters, speedometer readings

### Body Face: **Rajdhani** (Google Fonts — Free)
Used for: labels, body text, navigation, buttons, descriptions
- Feel: Technical, slightly condensed, reads at speed
- Weight: Regular (400), Medium (500), SemiBold (600)
- Usage: All prose text, labels, UI copy

### Data Face: **Share Tech Mono** (same as display)
Used for: stats tables, data readouts, timestamps
- Smaller sizes: 10–12sp
- ALL CAPS for labels, mixed case for values

### Type Scale
```
--text-xs:    10sp  // data labels, badges
--text-sm:    12sp  // secondary labels, timestamps
--text-base:  14sp  // body text, descriptions
--text-md:    16sp  // primary body, list items
--text-lg:    20sp  // section headers
--text-xl:    28sp  // screen titles
--text-2xl:   40sp  // scores, speeds (data hero)
--text-3xl:   64sp  // grade letter (S/A/B/C/D)
--text-4xl:   96sp  // speedometer reading
```

---

## Spacing System (8dp base grid)

```
--space-1:   4dp
--space-2:   8dp
--space-3:   12dp
--space-4:   16dp
--space-5:   20dp
--space-6:   24dp
--space-8:   32dp
--space-10:  40dp
--space-12:  48dp
--space-16:  64dp
```

---

## Shape & Borders

- **Border radius:** 0dp (sharp corners) for data cards and score displays
- **Border radius:** 4dp for buttons and inputs
- **Border radius:** 8dp for modals and bottom sheets
- **Borders:** 1dp solid `--color-matrix-muted` on cards
- **Glow effect:** `box-shadow: 0 0 12dp rgba(0,255,65,0.3)` on active/selected states
- **Scan lines:** Subtle repeating horizontal gradient overlay on hero sections (CSS/Canvas)

---

## Iconography

- Icon set: **Phosphor Icons** (free, available for Android via SVG import)
- Style: **bold** weight — reads at small sizes on dark backgrounds
- Color: `--color-matrix-dim` (#00C032) for active, `--color-text-secondary` for inactive
- Navigation icons: custom-designed for TripRank (speedometer, trophy, route, ghost avatar)

---

## Motion & Animation Principles

### The Three Rules:
1. **Data reveals always animate** — numbers count up, bars grow, never appear static
2. **Rank moments are cinematic** — rank-up and badge unlock get full-screen treatment
3. **Navigation is instant** — transitions are 200ms max, no page-load-style slides

### Specific Animations:

**Score Reveal (Trip End):**
```
Duration: 1500ms
Phase 1 (0–800ms): Matrix character rain fills the score area
Phase 2 (800–1200ms): Characters resolve into score digits
Phase 3 (1200–1500ms): Grade letter drops in from top with glow burst
Easing: ease-out for all phases
```

**Rank Progress Bar:**
```
Duration: 600ms
Easing: spring (stiffness: 300, damping: 30)
Glow pulses on completion of fill
```

**Badge Unlock:**
```
Duration: 2000ms total
Phase 1: Dark overlay, badge appears small at center
Phase 2: Badge scales to 1.0, particle burst (matrix green dots)
Phase 3: Badge name and description fade in below
Phase 4: Auto-dismiss or tap to dismiss
```

**Number Counters:**
```
Duration: 800ms
Easing: ease-out cubic
Step through intermediate values (not linear — accelerate at start, decelerate at end)
```

**Speedometer Needle:**
```
Real-time, 60fps update
Needle animates with slight lag (150ms) for natural feel — like a real analog gauge
```

---

## Scan Line Overlay

Applied to: Hero sections, HUD screen, trip end score area
```
// Compose DrawScope implementation:
// Draw semi-transparent horizontal lines every 4dp
// Opacity: 0.03 (very subtle — texture, not distraction)
// Color: #000000
```

---

## Signature UI Treatments

### "Terminal" Card Style
Cards that display data look like terminal printouts:
- Black background, `--color-border` hairline border
- Matrix-green left accent border (3dp) on active/hero cards
- Data labels: ALL CAPS, `--color-text-secondary`, 10sp, `Share Tech Mono`
- Data values: Mixed case, `--color-text-primary`, 20–28sp, `Share Tech Mono`

### Grade Letter Treatment
When showing A/B/C grades:
- 64sp `Share Tech Mono` Regular
- Color matches score tier (perfect = matrix green, poor = red)
- Subtle text-shadow glow: `0 0 20dp currentColor`
- Thin rectangular border (not circular — keeps the terminal feel)

### Speed Display
- Massive: 96sp, `Share Tech Mono`
- Unit label (KM/H) below in 12sp `Rajdhani SemiBold`, letter-spacing: 4dp
- Slight phosphor blur: `0 0 8dp rgba(0,255,65,0.5)` behind the number

---

## Component Visual Specs

### Bottom Navigation
```
Height: 56dp
Background: #0A0A0A with top border 1dp #1E1E1E
Active icon: matrix green (#00C032) + 2dp glow underline
Inactive icon: #444444
No labels (icons only — space is precious, icons are distinctive)
Active indicator: 2dp × 24dp green line above icon
```

### Primary Button
```
Height: 48dp
Background: transparent
Border: 1dp solid #00C032
Text: #00C032, 14sp Rajdhani SemiBold, ALL CAPS, letter-spacing 2dp
Pressed state: background #001A08, border #00FF41
Corner radius: 4dp
```

### Score Bar (Category Breakdown)
```
Height: 4dp
Background: #1A1A1A
Fill: gradient from score color (poor end) → score color (perfect end)
Glow: 0 0 6dp currentColor on fill
Corner radius: 0 (sharp, terminal style)
```

### Profile Avatar
```
Shape: Square with clipped corners (not circle)
Border: 2dp solid rank color
Corner radius: 4dp
Default: Generated from username initials, matrix green on black
```
