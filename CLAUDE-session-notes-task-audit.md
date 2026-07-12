# RevRank — Task Audit: Icon & Visual Fixes (June 29, 2026)

Full step-by-step visual fix guide with SVG replacements, CSS tweaks, and animation specs. Referenced from main `CLAUDE.md`.

## Inventory of Icon/Visual Issues

| # | Screen | Issue | Type |
|---|--------|-------|------|
| 1 | All bottom navs (shared CSS) | Tabs too flat, not enough contrast | CSS |
| 2 | b03-trip-end-score-87.html | Score circle has black border from track stroke | CSS |
| 3 | b04-home-dashboard.html (line 238) | Fire emoji `🔥` on streak stat — needs SVG | SVG inline |
| 4 | b07-pro-upsell-inline-gate.html (line 228) | Lock icon uses PNG that looks bad | SVG inline |
| 5 | b07-pro-activated.html (line 221) | Unlocked icon needs glow ✅ not plain "P" | HTML+CSS |
| 6 | b09-error-state.html (line 219) | Warning sign `⚠` emoji — needs custom error icon | SVG inline |
| 7 | b09-empty-no-badges.html (line 219) | Diamond `◇` is dim, needs glowing white | CSS |
| 8 | b09-loading-skeletons.html | No Matrix rain effect on skeleton loaders | CSS+JS |
| 9 | b00-slide-1-welcome.html (line 220) | Compass SVG too generic — needs futuristic design | SVG |
| 10 | b00-slide-2-how-you-drive.html (line 220) | Clock SVG too basic — needs depth | SVG |
| 11 | b00-vehicle-type.html (lines 222-229) | Emoji icons for sedan/rideshare/truck | SVG inline |
| 12 | b00-location-permission.html / notifications-permission.html | Missing screens (not yet created) | New files |
| 13 | b04-rank-up-enforcer.html | Rank badge icon too plain | CSS+SVG |
| 14 | b05-global-leaderboard.html / public-profile.html | Ranking icons need custom design | SVG |
列举更多   | b09-error-state.html (line 219) | Warning sign emoji — needs custom error icon | SVG inline |
| 15 | b09-empty-no-badges.html (line 219) | Diamond dim, needs glowing white | CSS |
| 16 | b09-loading-skeletons.html | No Matrix rain effect | CSS+JS |
| 17 | b00-slide-1-welcome.html | Compass SVG too generic | SVG |
| 18 | b00-slide-2-how-you-drive.html | Clock SVG too basic | SVG |
| 19 | b00-vehicle-type.html | Emoji icons for vehicle types | SVG |
| 20 | b04-rank-up-enforcer.html | Rank badge too plain | CSS+SVG |
| 21 | b05-global-leaderboard.html | Ranking icons need custom design | SVG |

---

## Step 1: Score Circle — Remove "Black Border" (b03-trip-end-score-87.html)

**Problem:** `.score-radial .track` uses `stroke: var(--track)` (#222). Against the black phone background (#000), this dark grey ring looks like a black border around the circle.

**Fix:** Change track stroke to transparent on the score circle:
```css
.score-radial .track{fill:none;stroke:transparent;stroke-width:8;stroke-linecap:round}
```
Or remove the track entirely — the fill ring alone with Matrix green glow is cleaner.

**Alternative:** Keep a subtle track using `stroke: rgba(255,255,255,0.06)` — provides slight definition.

---

## Step 2: Bottom Nav — Deeper Contrast (shared CSS)

**Fix:**
```css
/* Inactive tab — dimmer but visible */
.bottom-nav button { color: rgba(255,255,255,0.3); }
.bottom-nav button.active { color: var(--matrix); text-shadow: 0 0 10px var(--matrix-glow); }
/* Active SVG gets green glow fill */
.bottom-nav button.active svg { fill: var(--matrix); filter: drop-shadow(0 0 8px var(--matrix)); }
/* Background tint on active tab */
.bottom-nav button.active { background: rgba(0,255,65,0.08); }
```

---

## Step 3: Custom SVGs — Direct Inline Replacement

**Rule:** Every emoji or PNG icon gets replaced with an inline `<svg>` tag. All SVGs share:
- `viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"`
- Tint via parent `color` or `fill`/`stroke` attribute
- Size via `width` and `height` attributes or CSS

**Lock icon (pro-upsell)** — Replace `<img src="...">` with:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="width:1.2em;height:1.2em;vertical-align:middle">
  <!-- Hexagonal body -->
  <polygon points="12,3 20,7 20,17 12,21 4,17 4,7" />
  <!-- Angular shackle -->
  <path d="M9 9V7a3 3 0 016 0v2" />
  <!-- Keyhole notch -->
  <rect x="11" y="13" width="2" height="2" rx="0.5" />
  <circle cx="12" cy="12" r="1" />
</svg>
```

**Fire icon (home dashboard streak)** — Replace `🔥` with:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="width:1em;height:1em;vertical-align:-0.125em">
  <path d="M12 2S8 8 8 12a4 4 0 008 0c0-4-4-10-4-10z" />
  <path d="M12 16c-2 0-3-1-3-3 0-2 3-5 3-5s3 3 3 5c0 2-1 3-3 3z" />
  <path d="M10 18c-3 0-5-1.5-5-4 0-2.5 2-4.5 3-6" opacity="0.4" />
</svg>
```
列举更多 **Error icon (error state)** — Replace `⚠` with:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
  <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
  <line x1="12" y1="9" x2="12" y2="13" />
  <line x1="12" y1="17" x2="12.01" y2="17" />
</svg>
```

**Compass icon (welcome slide 1)**:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
  <circle cx="12" cy="12" r="10" />
  <path d="M16 8l-4 8-4-8 4 2z" />
  <circle cx="12嘟囔 retrain*" r="1" fill="currentColor" />
  <path d="M12 2v3M12 19v3M2 12h3M19 12h3" opacity="0.3" />
</svg>
```

**Clock icon (slide 2 — how you drive)** — HUD-style:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
  <circle cx="12" cy="12" r="9" />
  <circle cx="12" cy="12" r="10.5" opacity="0.3" />
  <!-- Tick marks -->
  <line x1="12" y1="3" x2="12" y2="4.5" />
  <line x1="12" y1="19.5" x2="12" y2="21" />
  <line x1="3" y1="12" x2="4.5" y2="12" />
  <line x1="19.5" y1="12" x2="21" y2="12" />
  <path d="M12 12l3 3" />
  <path d="M12 12l-2-4" />
  <circle cx="12" cy="12" r="1.5" fill="currentColor" />
</svg>
```
列举更多 **Vehicle icons (vehicle type screen):**
Sedan:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
  <path d="M5 17H3l1.5-4.5L6 9h12l1.5 3.5L21 17h-2" />
  <circle cx="7" cy="17" r="2" />
  <circle cx="17" cy="17" r="2" />
  <path d="M3 17h2M19 17h2" />
</svg>
```
Rideshare (same with taxi top light):
```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
  <path d="M5 17H3l1.5-4.5L6 9h12l1.5 3.5L21 17h-2" />
  <circle cx="7" cy="17" r="2" />
  <circle cx="17" cy="17" r="2" />
  <rect x="10" y="3" width="4" height="3" rx="1" />
</svg>
```
Truck/SUV:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
  <path d="M4 17H2l1-4 2-5h8l2 5h2l1 4h-2" />
  <rect x="14" y="5" width="6" height="5" rx="1" />
  <circle cx="6" cy="17" r="2" />
  <circle cx="16" cy="17" r="2" />
</svg>
```

**Diamond icon (empty badges)** — CSS fix, no replacement needed.

---

## Step 4: Empty Badges Diamond — Glowing White

**Fix:**
```css
color: rgba(255,255,255,0.3);
text-shadow: 0 0 20px rgba(255,255,255,0.2);
```
Or use the diamond SVG with proper glow:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.3)" stroke-width="1.5" style="width:48px;height:48px;filter:drop-shadow(0 0 12px rgba(255,255,255,0.15))">
  <path d="M12 2L2 12l10 10 10-10L12 2z" />
  <path d="M12 2L2 12h20L12 2z" fill="rgba(255,255,255,0.05)" />
  <path d="M12 22L2 12h20" />
</svg>
```

---

## Step 5: Loading Skeletons — Matrix Rain Effect

**File:** b09-loading-skeletons.html

**Add** a Canvas-based Matrix rain overlay behind the skeleton boxes:
```html
<canvas id="matrix-rain" style="position:absolute;top:0;left:0;width:100%;height:100%;opacity:0.15;pointer-events:none;z-index:0"></canvas>
```

JS script at bottom:
```javascript
(function(){
  const c = document.getElementById('matrix-rain');
  if(!c) return;
  const ctx = c.getContext('2d');
  let W, H;
  function resize(){ W=c.width=c.offsetWidth; H=c.height=c.offsetHeight; }
  resize();
  const cols = Math.floor(W/14);
  const drops = Array(cols).fill(1);
  const chars = 'アイウエオカキクケコサシスセソタチツテトナニヌネノ0123456789';
  function draw(){
    ctx.fillStyle='rgba(0,0,0,0.05)';
    ctx.fillRect(0,0,W,H);
    ctx.fillStyle='#00FF41';
    ctx.font='12px monospace';
    for(let i=0;i<drops.length;i++){
      const text = chars[Math.floor(Math.random()*chars.length)];
      ctx.fillText(text,i*14,drops[i]*14);
      if(drops[i]*14>H && Math.random()>0.975) drops[i]=0;
      drops[i]++;
    }
  }
  setInterval(draw, 50);
})();
```

---

## Step 6: Pro Lock → Unlock Animation (b07-pro-upsell-inline-gate → b07-pro-activated)

**Upsell lock (locked state):** Use hexagonal SVG lock (Step 3). Add subtle pulse animation:
```css
@keyframes lockPulse { 0%,100%{filter:drop-shadow(0 0 4px var(--matrix))} 50%{filter:drop-shadow(0 0 10px var(--matrix))} }
```

**Pro activated (unlocked state):** Replace plain "P" with the hexagonal lock morphing to checkmark:
```html
<div style="position:relative;width:100px;height:100px;display:flex;align-items:center;justify-content:center">
  <svg id="unlock-anim" viewBox="0 0 24 24" fill="none" stroke="var(--matrix)" stroke-width="1.5" style="width:64px;height:64px;filter:drop-shadow(0 0 20px var(--matrix))">
    <!-- Hexagonal body (fades out) -->
    <polygon id="lock-body" points="12,2 20,6 20,18 12,22 4,18 4,6" />
    <!-- Shackle (slides up) -->
    <path id="shackle" d="M9 9V7a3 3 0 016 0v2" />
    <!-- Checkmark (scales in) -->
    <path id="checkmark" d="M7 13l3 3 7-7" stroke-width="2.5" opacity="0" />
  </svg>
</div>
```

With CSS keyframe animation:
```css
@keyframes unlockSequence {
  0% { filter: drop-shadow(0 0 0 var(--matrix)); }
  20% { filter: drop-shadow(0 0 12px var(--matrix)); transform: rotate(3deg); }
  30% { transform: rotate(-3deg); }
  40% { transform: rotate(0); }
  50% { #shackle { transform: translateY(-8px); opacity: 0; } }
  60% { #lock-body { opacity: 0; } #checkmark { opacity: 1; transform: scale(1.3); } }
  70% { #checkmark { transform: scale(1); } }
  100% { filter: drop-shadow(0 0 20px var(--matrix)); }
}
```

Note: CSS-only approach triggers on page load. For Kotlin Compose, use `Animatable` with similar timing.

---

## Step 7: Tab Contrast — Deeper Styling

Full CSS from main file:
```css
.bottom-nav { 
  background: rgba(0,0,0,0.85); 
  backdrop-filter: blur(20px); 
  -webkit-backdrop-filter: blur(20px); 
  border-top: 1px solid rgba(0,255,65,0.15); 
}
.bottom-nav button { color: rgba(255,255,255,0.25); }
.bottom-nav button.active { 
  color: var(--matrix); 
  background: rgba(0,255,65,0.08); 
  text-shadow: 0 0 8px var(--matrix-glow); 
}
.bottom-nav button.active svg { 
  fill: var(--matrix); 
  filter: drop-shadow(0 0 8px var(--matrix)); 
}
.bottom-nav .nav-dot { 
  width: 6px; 
  height: 6px; 
  box-shadow: 0 0 8px var(--matrix); 
  background: var(--matrix); 
  opacity: 0; 
}
.bottom-nav button.active .nav-dot { opacity: 1; }
```

---

## Step 8: Rank-Up Badge & Leaderboard Icons

**Rank-up badge** (b04-rank-up-enforcer.html): Replace plain emoji/initials with SVG shield:
```html
<svg viewBox="0 0 24 24" fill="none" stroke="var(--matrix)" stroke-width="1.5" style="width:56px;height:56px">
  <path d="M12 2l3 4h5v5l4 3-4 3v5h-5l-3 4-3-4H4v-5l-4-3 4-3V6h5z" />
  <text x="12" y="14" text-anchor="middle" fill="var(--matrix)" stroke="none" style="font-family:var(--font-mono);font-size:8px">E</text>
</svg>
```

**Leaderboard rank icons** — Replace text-based rank numbers with SVG trophy/crown:
```html
<!-- Gold (1st) -->
<svg viewBox="0 0 24 24" fill="none" stroke="#FFD700" stroke-width="1.5" style="width:20px;height:20px">
  <path d="M6 9H4a2 2 0 01-2-2V5a2 2 0 012-2h2" />
  <path d="M18 9h2a2 2 0 002-2V5a2 2 0 00-2-2h-2" />
  <path d="M6 3h12v7a6 6 0 01-12 0z" />
  <path d="M12 16v5" />
  <path d="M8 21h8" />
</svg>
```

## Step 9: AI Image Generation Prompts

Tabular prompt reference stored in `design/gforce-asset-prompts.md`.

## Fix Reference Table

| Fix | File | Lines |
|-----|------|-------|
| Score circle track | `designs/b03-trip-end-score-87.html` | 149 |
| Fire icon | `designs/b04-home-dashboard.html` | 238 |
| Lock icon | `designs/b07-pro-upsell-inline-gate.html` | 228 |
| Pro activated unlock | `designs/b07-pro-activated.html` | 221 |
| Error icon | `designs/b09-error-state.html` | 219 |
| Diamond glow | `designs/b09-empty-no-badges.html` | 219 |
| Matrix rain skeletons | `designs/b09-loading-skeletons.html` | Add HTML+JS |
| Welcome compass | `designs/b00-slide-1-welcome.html` | 220 |
| Clock depth | `designs/b00-slide-2-how-you-drive.html` | 220 |
| Vehicle icons | `designs/b00-vehicle-type.html` | 222-229 |
| Bottom nav contrast | All screens with `.bottom-nav` | CSS |
| Rank-up badge | `designs/b04-rank-up-enforcer.html` | 203 |
| Leaderboard trophy | `designs/b05-global-leaderboard.html` | 174 |
| Lock animation | `designs/b07-pro-activated.html` | CSS |
| Profile nav icons | `designs/b05-public-profile.html` | Bottom nav |
