# RevRank Asset Prompts — Holographic Neon Wireframe Style

**Reference standard:** the green wireframe sportbike hero (`A_high-resolution,_detailed_vertical_image_202606290631.jpeg`). Every asset matches that look — a **real motorcycle rendered as a glowing neon polygonal wireframe mesh** on pure black, blooming headlight, horizontal CRT scanlines across the whole frame, a faint perspective wireframe grid receding behind the bike, and a green underglow reflection pooling on the glossy floor. Holographic X-ray blueprint aesthetic, not a solid glossy render.

**Master Style Block:** Copy the block for the color variant you want. Each background prompt below already has its color baked in — use this master block only when customizing outside the listed variants. **Two variants only: GREEN and RED. No blue.**

```
Green: holographic neon wireframe aesthetic, glowing MatrixGreen #00FF41 polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, X-ray blueprint hologram look, bright blooming white-green highlights where light sources sit (headlight, sensors), high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint green perspective wireframe grid receding into the darkness on both sides behind the subject, green light reflection pooling on a glossy black floor beneath the subject, volumetric green glow, sharp crisp continuous wireframe lines, photoreal proportions, no visible AI artifacts.

Red: holographic neon wireframe aesthetic, glowing race-red #FF003C polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, aggressive X-ray blueprint hologram look, bright blooming red-white highlights where light sources sit, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint red perspective wireframe grid receding into the darkness on both sides behind the subject, red light reflection pooling on a glossy black floor beneath the subject, volumetric red glow, danger/alarm race aesthetic, sharp crisp continuous wireframe lines, photoreal proportions, no visible AI artifacts.
```

**Aspect Ratios:**
- **Backgrounds (bg-*)** → **9:16 vertical** (1536x2752, same as reference hero)
- **Icons (icon-*)** → **1:1 square** (1536x1536)

**IMPORTANT — Icon Design Rules:**
- Icons keep the same **neon wireframe glow language** (lines glow from within, bright #00FF41) BUT are stripped for clean isolation.
- **NO CRT scanlines, NO background wireframe grid, NO floor reflection, NO particles** on icons — those effects belong only to backgrounds and would ruin background removal.
- Icons MUST have a **clean, solid #000000 background** — the neon glow is baked INTO the subject's stroke edges, staying within 2-3px of each line, not floating as atmosphere.
- **Subject must be crisp, sharp-edged, high contrast** — easy for icon-forge / background removal to isolate cleanly onto the app interfaces.
- Think "holographic dashboard warning light" — self-contained, sharp, glows from within.

**Background Mapping by Screen — every screen a DIFFERENT bike (no repeated silhouettes):**
| Screen | Bike / Scene | Color |
|--------|--------------|-------|
| Sign-in | Supersport hero (existing JPEG, keep) | GREEN |
| Home | Cruiser side profile | GREEN |
| Profile | Adventure / touring 3/4 | GREEN |
| Leaderboard / Challenges | Cafe racer profile | GREEN |
| History | Rider POV road | GREEN |
| Rank | Engine detail macro | GREEN |
| Live HUD / Tracking | Supersport front 3/4 | RED |
| Trip Score | Streetfighter low angle | RED |
| G-force | Track bike cornering lean | RED |
| Analytics | Dragbike rear 3/4 launch | RED |
| Route Replay | Front brake disc / fork macro | RED |
| Loading, Error, Empty | Any (mix per mood) | — |

**Icons always stay MatrixGreen #00FF41.** Background = atmosphere (green or red), icons = UI chrome (green).

---

## BACKGROUNDS (9:16 vertical — 1536x2752)
Full holographic wireframe scenes with scanlines, background grid, and floor reflection. Composited backgrounds — NOT for isolation (unlike icons).

**Note:** The sign-in hero (`A_high-resolution,_detailed_vertical_image_202606290631.jpeg`) is the reference quality standard. Do not regenerate it.

---

> **Every background is a DIFFERENT bike/scene — no silhouette repeats.** 5 GREEN (brand/resting) + 5 RED (active/performance).

### GREEN 1. Cruiser Profile
**File:** `bg-cruiser-profile.jpeg` — 9:16 vertical (Green, for Home)

```
Style: holographic neon wireframe aesthetic, glowing MatrixGreen #00FF41 polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, X-ray blueprint hologram look, bright blooming white-green headlight, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint green perspective wireframe grid receding into darkness behind the bike, green light reflection pooling on a glossy black floor beneath, volumetric green glow, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: a real cruiser motorcycle (Harley-Davidson / Indian class) viewed from the right side in full profile, long low stance, stretched wheelbase, thick rear tyre, pulled-back handlebars, teardrop fuel tank, big V-twin engine, spoked wheels and fenders — the ENTIRE bike rendered as a glowing green polygonal wireframe mesh, every curve of the tank, fender, and exhaust traced in fine mesh lines. Round headlight blooms bright white-green.

Atmosphere: pure black background, faint green wireframe grid on the ground plane and receding behind, fine horizontal scanlines across the full frame, long green underglow reflection mirrored on the glossy black floor beneath the wheels.

Composition: bike centered horizontally, occupying lower 55% of frame, side profile facing right, long silhouette stretching across the frame, ground line around 75% from top, dark empty space above for text/logo overlay.

Output: holographic wireframe hologram, dramatic green-on-black, long low cruiser silhouette matching the reference style.
```

---

### GREEN 2. Adventure / Touring 3/4
**File:** `bg-adventure-touring.jpeg` — 9:16 vertical (Green, for Profile)

```
Style: holographic neon wireframe aesthetic, glowing MatrixGreen #00FF41 polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, X-ray blueprint hologram look, bright blooming white-green headlight, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint green perspective wireframe grid receding into darkness behind the bike, green light reflection pooling on a glossy black floor beneath, volumetric green glow, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: a real adventure / touring motorcycle (BMW GS / Honda Africa Twin class) viewed from a front-right 3/4 angle, tall upright rugged stance, high beak fender, large windscreen, knobby dual-sport tyres, long-travel suspension, hard pannier luggage cases, spoked wheels — the ENTIRE bike rendered as a dense glowing green polygonal wireframe mesh, every panel, spoke, tyre knob, and pannier edge traced in fine mesh lines. Round/rectangular headlight blooms bright white-green.

Atmosphere: pure black background, faint green perspective wireframe grid fanning behind and receding to a vanishing point, fine horizontal scanlines across the full frame, green underglow reflection mirrored on the glossy black floor beneath the tyres.

Composition: bike occupies lower 60% of frame rising from bottom-center, tall stance, headlight around 40% from top, negative space above for overlay.

Output: holographic wireframe hologram, green-on-black, rugged tall adventure-bike silhouette — clearly distinct from the low sportbike.
```

---

### GREEN 3. Cafe Racer Profile
**File:** `bg-cafe-racer.jpeg` — 9:16 vertical (Green, for Leaderboard / Challenges)

```
Style: holographic neon wireframe aesthetic, glowing MatrixGreen #00FF41 polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, X-ray blueprint hologram look, bright blooming white-green headlight, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint green perspective wireframe grid receding into darkness behind the bike, green light reflection pooling on a glossy black floor beneath, volumetric green glow, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: a real retro cafe racer motorcycle (Triumph Thruxton / classic Honda class) viewed from the left side in full profile, minimalist vintage stance, round single headlight, slim teardrop tank, low clip-on handlebars, bum-stop single seat, exposed twin-shock rear, wire-spoke wheels, twin megaphone exhausts — the ENTIRE bike rendered as a clean glowing green polygonal wireframe mesh, every classic curve of tank, seat cowl, and pipe traced in fine mesh lines. Round headlight blooms bright white-green.

Atmosphere: pure black background, faint green wireframe grid on the ground plane and receding behind, fine horizontal scanlines across the full frame, green underglow reflection mirrored on the glossy black floor beneath the wheels.

Composition: bike centered horizontally, occupying lower 50% of frame, side profile facing left, compact silhouette, ground line around 72% from top, empty space above for overlay.

Output: holographic wireframe hologram, green-on-black, slim vintage cafe-racer silhouette — clearly distinct from the cruiser and sportbike.
```

---

### GREEN 4. Rider POV Road
**File:** `bg-rider-pov-road.jpeg` — 9:16 vertical (Green, for History)

```
Style: holographic neon wireframe aesthetic, glowing MatrixGreen #00FF41 wireframe on a pure black #000000 background, X-ray blueprint hologram look, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint green perspective wireframe grid, volumetric green glow, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: first-person rider view over a real sportbike instrument cluster/TFT rendered in green wireframe, showing a neon-green rev counter and speed readout, handlebar grips and mirrors traced as green mesh at the bottom corners, dark road stretching ahead drawn as a green wireframe grid with glowing lane markers converging at a vanishing point.

Atmosphere: road ahead built from a green perspective wireframe grid cutting through darkness, distant city skyline as a faint green wireframe silhouette on the horizon, fine horizontal scanlines across the full frame, tunnel-like vignette darkening the frame edges, brighter green ambient light down the center of the road.

Composition: instrument cluster in the lower 20% of frame (soft-focus foreground), wireframe road occupying the middle 60%, horizon skyline in the upper 20%, green light cone converging at the vanishing point.

Output: holographic wireframe hologram, first-person immersive perspective, green-lit wireframe cockpit and road.
```

---

### GREEN 5. Engine Detail Macro
**File:** `bg-engine-detail.jpeg` — 9:16 vertical (Green, for Rank)

```
Style: holographic neon wireframe aesthetic, glowing MatrixGreen #00FF41 polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, X-ray blueprint hologram look, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint green perspective wireframe grid receding behind, volumetric green glow, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: extreme close-up of a real high-performance motorcycle engine — angled cylinder head with cooling fins, braided brake line, intake duct, gearbox casing, exposed cam chain — the whole mechanical assembly rendered as a dense glowing green polygonal wireframe mesh, every fin, bolt, and casting edge traced in fine mesh lines.

Atmosphere: macro detail, shallow depth of field with the main mechanical mass in sharp wireframe focus and the rest dissolving into pure black, faint green wireframe grid receding behind, fine horizontal scanlines across the full frame, subtle green underglow beneath the engine.

Composition: engine fills 70-80% of frame, extreme close-up, low angle looking up, dramatic green glow from behind and below highlighting the fins and fasteners.

Output: holographic wireframe hologram, extreme macro detail, green mesh tracing every mechanical edge.
```

---

### RED 1. Supersport Front 3/4
**File:** `bg-sportbike-front-red.jpeg` — 9:16 vertical (Red, for Live HUD / Tracking)

```
Style: holographic neon wireframe aesthetic, glowing race-red #FF003C polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, aggressive X-ray blueprint hologram look, bright blooming red-white headlight, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint red perspective wireframe grid receding into darkness on both sides behind the bike, red light reflection pooling on a glossy black floor beneath, volumetric red glow, danger/alarm race aesthetic, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: a real modern supersport motorcycle (Kawasaki Ninja / Yamaha R1 class) viewed from a front-left 3/4 angle, aggressive race stance, twin angular fairings, sharp beak nose, forks and brake discs visible — the ENTIRE bike rendered as a dense glowing red polygonal wireframe mesh, every panel, spoke, tyre tread, and engine detail traced in fine mesh lines. One headlight blooms bright red-white like a warning light.

Atmosphere: pure black background, faint red perspective wireframe grid fanning behind the bike and receding to a vanishing point, fine horizontal scanlines across the full frame, bright red underglow reflection mirrored on the glossy black floor beneath the tyres.

Composition: bike occupies lower 60% of frame rising from bottom-center, headlight around 45% from top, negative space above for overlays. Floor reflection at the very bottom.

Output: holographic wireframe hologram, aggressive red-on-black. Feels like race mode / high alert.
```

---

### RED 2. Streetfighter Low Angle
**File:** `bg-streetfighter-red.jpeg` — 9:16 vertical (Red, for Trip Score)

```
Style: holographic neon wireframe aesthetic, glowing race-red #FF003C polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, aggressive X-ray blueprint hologram look, bright blooming red-white LED headlight, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint red perspective wireframe grid receding into darkness behind the bike, red light reflection pooling on a glossy black floor beneath, volumetric red glow, danger/alarm race aesthetic, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: a real hypernaked streetfighter motorcycle (KTM Super Duke / Ducati Streetfighter / Yamaha MT class) from a front-left 3/4 low angle looking up, upright aggressive stance, exposed trellis frame and big engine block, muscular sculpted tank, upswept tail, single stacked LED headlight — the ENTIRE bike rendered as a dense glowing red polygonal wireframe mesh, frame, engine fins, radiator, and wheels all traced in fine mesh lines. Headlight blooms bright red-white.

Atmosphere: pure black background, faint red wireframe grid fanning behind the bike, fine horizontal scanlines across the full frame, red underglow reflection mirrored on the glossy black floor.

Composition: bike centered, occupying lower 65%, low angle looking up, engine block around 50% from top, headlight around 30% from top, empty space in the upper third.

Output: holographic wireframe hologram, aggressive red-on-black. Feels like results / intensity / score moment.
```

---

### RED 3. Track Bike Cornering Lean
**File:** `bg-track-lean-red.jpeg` — 9:16 vertical (Red, for G-force)

```
Style: holographic neon wireframe aesthetic, glowing race-red #FF003C polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, aggressive X-ray blueprint hologram look, bright blooming red-white headlight, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint red perspective wireframe grid receding into darkness behind the bike, red light reflection streaking on a glossy black floor beneath, volumetric red glow, motion-blur speed lines, danger/alarm race aesthetic, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: a real MotoGP-style track superbike (race fairings, no mirrors, slick tyres) captured mid-corner at extreme lean angle, knee-down cornering pose with a rider tucked in, the bike tilted hard to the left as if railing an apex — the ENTIRE bike and rider rendered as a dense glowing red polygonal wireframe mesh, fairings, wheels, and lean angle traced in fine mesh lines. Headlight/number-plate area blooms bright red-white.

Atmosphere: pure black background, faint red wireframe grid tilted with the corner and receding, fine horizontal scanlines across the full frame, red speed streaks trailing the bike, red underglow smearing across the glossy black floor.

Composition: bike leaning into the frame from lower-center, tilted diagonal composition to emphasise lean, occupying lower-to-mid 60%, dynamic negative space in the upper corner for overlay.

Output: holographic wireframe hologram, intense red-on-black, extreme dynamic lean — clearly distinct from the upright sportbike and streetfighter.
```

---

### RED 4. Dragbike Rear 3/4 Launch
**File:** `bg-dragbike-rear-red.jpeg` — 9:16 vertical (Red, for Analytics)

```
Style: holographic neon wireframe aesthetic, glowing race-red #FF003C polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, aggressive X-ray blueprint hologram look, bright blooming red taillight, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint red perspective wireframe grid receding into darkness ahead of the bike, red light reflection pooling on a glossy black floor beneath, volumetric red glow, danger/alarm race aesthetic, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: a real drag-strip motorcycle (long stretched swingarm, wheelie bar, fat drag slick rear tyre) viewed from a rear-left 3/4 angle as if launching away from the viewer down a strip, the wide rear tyre and stretched swingarm dominating the foreground — the ENTIRE bike rendered as a dense glowing red polygonal wireframe mesh, rear tyre tread, swingarm, exhaust, and tail traced in fine mesh lines. Taillight blooms bright red.

Atmosphere: pure black background, faint red perspective wireframe grid receding ahead to a vanishing point (the strip), fine horizontal scanlines across the full frame, red underglow reflection mirrored on the glossy black floor beneath the fat rear tyre.

Composition: fat rear tyre and swingarm in the lower-foreground, bike receding toward upper-center, strip grid vanishing at the top, negative space upper area for overlay.

Output: holographic wireframe hologram, red-on-black, rear-view launch perspective — clearly distinct from all front/side bikes.
```

---

### RED 5. Front Brake Disc / Fork Macro
**File:** `bg-brake-macro-red.jpeg` — 9:16 vertical (Red, for Route Replay)

```
Style: holographic neon wireframe aesthetic, glowing race-red #FF003C polygonal mesh lines tracing every surface of the subject on a pure black #000000 background, aggressive X-ray blueprint hologram look, high contrast neon-on-black, fine horizontal CRT scanlines across the entire frame at low opacity, faint red perspective wireframe grid receding behind, volumetric red glow, danger/alarm race aesthetic, sharp crisp continuous wireframe lines, no visible AI artifacts.

Aspect ratio: 9:16 vertical, 1536x2752 pixels, portrait orientation.

Subject: extreme close-up of a real sportbike front end — twin drilled brake discs, radial brake caliper, front fork tubes, wheel spokes, and tyre edge — the whole front assembly rendered as a dense glowing red polygonal wireframe mesh, every disc drill-hole, caliper piston, and spoke traced in fine mesh lines. Distinct from the engine macro: this is the wheel/brake/fork, not the engine.

Atmosphere: macro detail, shallow depth of field with the brake disc and caliper in sharp wireframe focus and the rest dissolving into pure black, faint red wireframe grid receding behind, fine horizontal scanlines across the full frame, subtle red underglow beneath.

Composition: brake disc and caliper fill 70-80% of frame, extreme close-up, slightly low angle, dramatic red glow rimming the disc and fork.

Output: holographic wireframe hologram, intense red macro detail of the front wheel/brake — mechanically distinct from the green engine macro.
```

---

## ICONS (1:1 square — 1536x1536)
All icons stay **MatrixGreen #00FF41**. Icons keep the neon-wireframe glow language but **drop all scanlines, background grid, floor reflection, and particles** — solid #000000 background with zero floating elements for clean icon-forge extraction onto the app interfaces.

---

### Nav Icons (24x24 display)

#### Track — Radar Crosshair
**File:** `icon-nav-track.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: radar crosshair icon in cyberpunk HUD style, two concentric thin circles with four crosshair tick marks at 0/90/180/270 degrees, a thin hairline cross intersecting at dead center with a small bright dot at the intersection. All strokes sharp, clean, continuous.

Critical: pure solid #000000 background. NO scanlines, NO matrix rain, NO floating particles, NO atmospheric effects, NO background texture. The neon green glow is contained within the stroke lines — a subtle halo no more than 2-3 pixels beyond each stroke. The subject is the ONLY element in the frame.

Glow: lines are bright #00FF41 glowing from within, halo tight to the stroke edges. The center dot is slightly brighter than the circle lines.

Output: sharp, crisp, minimalist icon on pure black. Dashboard indicator quality. Clean enough for automatic background removal to produce perfect transparency.
```

---

#### History — Clock
**File:** `icon-nav-history.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: analog clock icon in cyberpunk HUD style, a perfect circle clock face with short hash tick marks at 12, 3, 6, and 9 o'clock, two hands — a short hour hand at roughly 10 o'clock and a long minute hand at roughly 2 o'clock, a small center dot at the pivot. All strokes clean and continuous.

Critical: pure solid #000000 background. NO scanlines, NO matrix rain, NO floating particles, NO atmospheric effects, NO background texture. Neon glow tightly contained within the stroke edges — halo no more than 2-3 pixels. Hands distinct from the circle rim.

Glow: circle and hands glow #00FF41 with the glow tight to the lines. The center dot is a bright solid point. Hands slightly brighter than the rim.

Output: sharp, clean, minimalist dashboard icon on pure black. Ready for clean background removal.
```

---

#### Rank — Trophy
**File:** `icon-nav-rank.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: angular trophy cup icon in cyberpunk style, geometric triangular cup body with two angular handles from the sides, a small rectangular base beneath, the cup opening a straight horizontal line, the whole shape built from straight lines and sharp angles — no curves. Faceted crystal trophy as glowing wireframe.

Critical: pure solid #000000 background. NO scanlines, NO particles, NO matrix rain, NO background elements, NO floating debris. Green glow baked into the stroke edges only, 2-3px halo. The cup interior is dark/empty — only the outline glows.

Glow: trophy outline glows bright #00FF41, slightly brighter at the cup rim and handle attachment points.

Output: sharp, minimalist, geometric trophy icon on solid black. Clean vector-like edges. Ready for perfect background separation.
```

---

#### Profile — Person
**File:** `icon-nav-profile.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: minimalist user/profile icon in cyberpunk style, a simple circle for the head and geometric angular shoulders/torso below forming a person silhouette. Plain circle head (no facial features), straight angular shoulders sloping from the neck, a short vertical neck line. Clean, faceted, geometric.

Critical: pure solid #000000 background. NO scanlines, NO particles, NO matrix rain, NO background effects. Neon glow within the stroke edges — under 3px halo. A simple glowing outline, no fill.

Glow: consistent #00FF41 glow across all outline segments, slightly brighter at the top of the head circle. Uniform stroke thickness.

Output: crisp, recognizable person silhouette icon on pure black. Simple HUD player marker. Clean edges for perfect background removal.
```

---

### G-Force Metric Icons (20x20 display)

#### Lateral G — Left-Right Arrows
**File:** `icon-metric-lateral.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: horizontal double arrow icon, two arrows pointing in opposite directions (left and right), side by side or overlapping at center. Each arrow has a straight horizontal shaft and an angular arrowhead (two straight lines meeting at a point). Left arrow points left, right arrow points right. Sharp angular arrowheads, flat-cut shaft ends.

Critical: pure solid #000000 background. NO scanlines, NO floating particles, NO matrix rain, NO background effects. Green glow within 2-3px of the stroke edges. Straight lines only — no curves, no rounded caps. The center intersection may glow slightly brighter.

Glow: even #00FF41 glow across all arrow lines, arrowhead tips slightly brighter. No fill inside the arrows — outline only.

Output: sharp, symmetrical, minimalist double-arrow icon on solid black. HUD calibration marker. Ready for clean background removal.
```

---

#### Longitudinal G — Up-Down Arrows
**File:** `icon-metric-longitud.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: vertical double arrow icon, two arrows pointing in opposite directions (up and down), stacked vertically. Each arrow has a straight vertical shaft and an angular arrowhead (two straight lines meeting at a point). Up arrow points up, down arrow points down. Sharp angular arrowheads, flat-cut shaft ends, shared center point or a small gap.

Critical: pure solid #000000 background. NO scanlines, NO floating particles, NO matrix rain, NO background elements. Neon glow within the stroke edges. All straight lines — no curves. Up/down communicate throttle (up) and brake (down).

Glow: even #00FF41 glow across all arrow lines, slightly brighter at the arrowhead tips. No fill inside the arrows.

Output: sharp, minimalist vertical double-arrow icon on solid black. HUD brake/throttle indicator. Clean edges for perfect background removal.
```

---

#### Safe Zone — Shield
**File:** `icon-metric-safezone.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: angular shield icon in cyberpunk style, shield outline built from straight lines — a pentagonal/hexagonal shield (flat top, angled sides tapering to a bottom point). Inside, a simple checkmark (two lines meeting at an angle, classic V). Shield outline and checkmark both glowing neon #00FF41 strokes.

Critical: pure solid #000000 background. NO scanlines, NO floating particles, NO matrix rain, NO background effects. NO glow bleed beyond 2-3px of the stroke lines. Checkmark distinctly visible inside the shield, centered, not touching the edges. Straight lines, no curves.

Glow: shield outline and checkmark glow #00FF41, checkmark slightly brighter than the outline. Subtle extra glow at the bottom shield point.

Output: sharp, clean shield + checkmark icon on pure black. Security verification badge. Clean for perfect background separation.
```

---

### PRO Badge (20x20 display)

#### Locked Padlock
**File:** `icon-pro-lock.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: angular padlock icon in cyberpunk style, rectangular lock body with straight edges and sharp corners (no rounded rectangle), an angular U-shaped shackle on top with straight sides and flat top, a small keyhole notch (rectangle or diamond) centered in the body. Straight lines and right angles only — no curves. Digital/hardware lock.

Critical: pure solid #000000 background. NO scanlines, NO particles, NO matrix rain, NO floating glow dots, NO background effects. Glow strictly within the stroke edges — 2-3px halo max. The shackle is clearly separated from the body — the opening between them is visible dark empty space.

Glow: bright #00FF41 glow on all lock edges. The keyhole interior slightly illuminated with a fainter inner green glow. Shackle top corners slightly brighter.

Output: sharp, angular, geometric padlock icon on solid black. Clean, distinct shackle and body. Ready for perfect transparency extraction.
```

---

### Analytics Icons (20x20 display)

#### Acceleration — Forward Arrow + Meter
**File:** `icon-analytic-accel.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: acceleration icon in cyberpunk style, a forward-pointing (rightward) arrow with a small semicircular gauge/dial near the arrowhead. The arrow has a straight horizontal shaft and an angular arrowhead. The gauge is a semicircular arc near the arrowhead with a small needle pointing right within it. Straight lines and clean circular arcs only — sharp, technical.

Critical: pure solid #000000 background. NO scanlines, NO floating particles, NO matrix rain, NO background elements. Glow within the stroke edges — 2-3px halo max. The gauge needle clearly visible against the arc.

Glow: clean #00FF41 glow across all elements, needle slightly brighter than the arc and shaft.

Output: sharp, technical, minimalist acceleration icon on pure black. Telemetry symbol. Clean edges for background removal.
```

---

#### Braking — Downward Arrow + Brake Disc
**File:** `icon-analytic-brake.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: braking icon in cyberpunk style, a downward-pointing arrow intersecting a circular brake disc outline. The arrow points straight down with an angular arrowhead at the bottom. The brake disc is a complete thin ring (hollow center) behind/intersecting the shaft, the shaft passing through the disc center. Clean geometric shapes — straight vertical shaft, V arrowhead, perfect circle disc.

Critical: pure solid #000000 background. NO scanlines, NO particles, NO matrix rain, NO background effects. Glow tight to the stroke edges. Brake disc a clean ring — no internal lines or cross-drilling, keep it simple for clean isolation.

Glow: clean #00FF41 glow on arrow and disc, arrow slightly brighter than the disc. Subtle extra glow where the shaft crosses the disc center.

Output: sharp, clean brake icon on solid black. Technical schematic style. Ready for clean background removal.
```

---

#### Smoothness — S-Curve Road
**File:** `icon-analytic-smooth.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: smooth cornering icon, an S-curve road from a top-down overhead view. The road is two parallel sweeping lines curving left then right in a smooth S, like a winding road from above. Continuous flowing bezier-style curves — no sharp angles. Between the lines, a dashed centerline runs along the S.

Critical: pure solid #000000 background. NO scanlines, NO particles, NO matrix rain, NO background effects. Glow within the stroke edges at 2-3px max. Only the two outer road lines and the dashed centerline. Curves smooth and flowing, not jagged.

Glow: graceful #00FF41 glow along the road lines, outer lines slightly brighter than the center dashes. A continuous flowing glow like a neon tube bent into an S.

Output: elegant, flowing S-curve icon on pure black. Top-down road map style. Clean lines for perfect background separation.
```

---

### Empty State Icons (48x48 display)

#### Empty Badge — Locked Hexagon
**File:** `icon-empty-badge.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: empty badge slot icon in cyberpunk style. A regular hexagon outline (six equal sides) with a dashed/dotted border (suggesting an empty slot). Inside, a small simple padlock outline at low brightness (locked/unavailable), much smaller than the hexagon, same angular straight-line style.

Critical: pure solid #000000 background. NO scanlines, NO particles, NO matrix rain, NO background effects. Hexagon border is dashed (short segments with gaps). Inner padlock small, simple, deliberately dimmer than the hexagon. The void inside is completely empty black — no fill.

Glow: hexagon dashed border glows #00FF41 at moderate brightness. Inner padlock glows same color at ~40% brightness (dim). Each dash glows individually; gaps have no glow.

Output: clean dashed hexagon with dim inner lock on solid black. Waiting/empty state. Perfect for transparent extraction.
```

---

#### No Data — Radar Dish
**File:** `icon-empty-nodata.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: no-signal/empty data icon in cyberpunk style. A small radar/satellite dish (parabolic curve with a stem/base) pointing up and slightly right. Above it, 2-3 concentric dashed arcs for signal waves — incomplete (dashed), with a break in the outermost arc, suggesting "no signal / searching."

Critical: pure solid #000000 background. NO scanlines, NO floating particles, NO matrix rain, NO background effects. Dish is a simple semicircular curve with a straight stem. Signal arcs are concentric partial circles, dashed with gaps. The broken outer arc conveys "no data."

Glow: dish glows #00FF41 at moderate brightness. Signal arcs glow #00FF41 but dim — brighter at the inner arc, very dim at the broken outer arc. The gap in the outer arc is distinctly dark.

Output: clean, simple radar dish icon on solid black. "No signal" clear at a glance. Ready for clean transparency extraction.
```

---

### Utility Icons (20x24 / 24x24 display)

#### Back — Left Chevron
**File:** `icon-utility-back.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: left-pointing chevron arrow in cyberpunk style. Two straight line segments meeting at an acute angle forming a < shape — simple, clean, angular. Line thickness moderate (~4-5% of frame width). Each end flat-cut (not rounded). Centered in the frame.

Critical: pure solid #000000 background. NO scanlines, NO particles, NO matrix rain, NO background effects. Glow tightly contained within the stroke edges. Two straight lines only — no dots, no crossbar. Minimalist.

Glow: clean #00FF41 glow along both segments. The vertex where they meet glows slightly brighter. Open ends clean and flat.

Output: minimalist, sharp left chevron on pure black. Simple navigation indicator. Clean for perfect background removal.
```

---

#### Settings — Gear
**File:** `icon-utility-settings.jpeg` — 1:1 square

```
Style: holographic neon wireframe icon, glowing MatrixGreen #00FF41 lines on a pure solid #000000 background, sharp crisp continuous strokes that glow from within, high contrast neon-on-black. NO scanlines, NO background grid, NO floor reflection, NO particles — clean isolated icon only.

Aspect ratio: 1:1 square, 1536x1536 pixels.

Subject: gear/cog icon in cyberpunk style. A gear with exactly 6 straight teeth evenly spaced around a circular ring, thick ring body, rectangular teeth with straight parallel sides and flat tops (no rounded teeth), hollow circle center. Straight lines and circular arcs only.

Critical: pure solid #000000 background. NO scanlines, NO floating particles, NO matrix rain, NO background effects. Glow within the stroke edges. Symmetrical — every tooth identical. Center hole empty black. No fill — outline/stroke design.

Glow: even #00FF41 glow across the entire outline, equal per tooth. Outer rim slightly brighter than the inner center-hole edge.

Output: sharp, symmetrical, 6-tooth gear icon on pure black. Technical mechanical aesthetic. Clean edges for perfect background removal.
```

---

## COLOR SCHEME VARIANTS (for icons)
Icons default to MatrixGreen. To generate a variant set, find-and-replace in any icon prompt:

| Find | Replace With | Scheme Name |
|------|-------------|-------------|
| `#00FF41` and `green` | `#FF003C` and `red` | Red Matrix (aggressive) |

*(Blue variant removed — RevRank runs green + red only.)*

---

## GENERATION CHECKLIST

### Backgrounds (9:16 vertical — 1536x2752)
**Green (brand/resting — each a different bike):**
- [ ] `bg-cruiser-profile.jpeg` — Cruiser side profile → Home
- [ ] `bg-adventure-touring.jpeg` — Adventure/touring 3/4 → Profile
- [ ] `bg-cafe-racer.jpeg` — Cafe racer profile → Leaderboard/Challenges
- [ ] `bg-rider-pov-road.jpeg` — Rider first-person road → History
- [ ] `bg-engine-detail.jpeg` — Extreme macro engine → Rank

**Red (active/performance — each a different bike):**
- [ ] `bg-sportbike-front-red.jpeg` — Supersport front-3/4 → Live HUD
- [ ] `bg-streetfighter-red.jpeg` — Streetfighter low angle → Trip Score
- [ ] `bg-track-lean-red.jpeg` — Track bike cornering lean → G-force
- [ ] `bg-dragbike-rear-red.jpeg` — Dragbike rear 3/4 launch → Analytics
- [ ] `bg-brake-macro-red.jpeg` — Front brake disc/fork macro → Route Replay

### Nav Icons (1:1 square — 1536x1536, display 24x24) — Always GREEN
- [ ] `icon-nav-track.jpeg` — Radar crosshair
- [ ] `icon-nav-history.jpeg` — Clock
- [ ] `icon-nav-rank.jpeg` — Trophy
- [ ] `icon-nav-profile.jpeg` — Person silhouette

### G-Force Icons (1:1 square — 1536x1536, display 20x20) — Always GREEN
- [ ] `icon-metric-lateral.jpeg` — Left-right arrows
- [ ] `icon-metric-longitud.jpeg` — Up-down arrows
- [ ] `icon-metric-safezone.jpeg` — Shield

### PRO Badge (1:1 square — 1536x1536, display 20x20) — Always GREEN
- [ ] `icon-pro-lock.jpeg` — Padlock

### Analytics Icons (1:1 square — 1536x1536, display 20x20) — Always GREEN
- [ ] `icon-analytic-accel.jpeg` — Acceleration
- [ ] `icon-analytic-brake.jpeg` — Braking
- [ ] `icon-analytic-smooth.jpeg` — S-curve smoothness

### Empty State (1:1 square — 1536x1536, display 48x48) — Always GREEN
- [ ] `icon-empty-badge.jpeg` — Locked hexagon
- [ ] `icon-empty-nodata.jpeg` — Radar dish no-signal

### Utility Icons (1:1 square — 1536x1536, display 20x24 / 24x24) — Always GREEN
- [ ] `icon-utility-back.jpeg` — Left chevron
- [ ] `icon-utility-settings.jpeg` — 6-tooth gear
```