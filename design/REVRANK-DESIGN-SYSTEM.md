# REVRANK — Cyberpunk CRT Terminal Design System

**Version:** 2.0
**Status:** Active specification for all frontend views, components, and states.
**Aesthetic:** Retro-futuristic, cyberpunk, CRT terminal HUD.

---

## 1. Global Visual Theme — The Core Aesthetic

### 1.1 Design Tokens

```css
:root {
  /* Background */
  --bg-base: #000000;

  /* Primary Accent (Phosphor Glow) */
  --accent-green: #00FF66;

  /* Typography Base */
  --font-terminal: 'Fira Code', 'JetBrains Mono', 'Courier New', monospace;
}
```

| Token | Value | Usage |
|-------|-------|-------|
| `--bg-base` | `#000000` | Pure absolute black background |
| `--accent-green` | `#00FF66` | Primary phosphor green — no washed-out neon |
| `--font-terminal` | `'Fira Code', monospace` | All text, labels, numbers |

### 1.2 Color Palette

| Role | Color | Hex |
|------|-------|-----|
| Background | Pure black | `#000000` |
| Surface | Near-black | `#050505` — `#1A1A1A` |
| Phosphor primary | Vibrant green | `#00FF66` |
| Phosphor dim | Muted green | `#00CC44` |
| Phosphor dark | Background green | `#003D1A` |
| Text primary | Light gray | `#E8E8E8` |
| Text dim | Mid gray | `#888888` |
| Track / faint | Dark gray | `#555555` / `#333333` |
| Score green (90+) | Green | `#00FF41` |
| Score lime (75+) | Lime | `#ADFF2F` |
| Score amber (55+) | Gold | `#FFD700` |
| Score orange (35+) | Orange | `#FF6B00` |
| Score red (<35) | Red | `#FF453A` |
| Metric cyan | Distance | `#00B4D8` |
| Metric orange | G-force warning | `#FF6B00` |
| Metric yellow | Braking | `#FFD700` |

### 1.3 Typography

All text uses clean monospaced terminal fonts. No serif, no sans-serif body fonts.

```css
--font-mono:      'JetBrains Mono', 'Fira Code', 'Courier New', monospace;
--font-body:      'Rajdhani', 'Arial', sans-serif;
--font-display:   'Orbitron', 'Rajdhani', sans-serif;
--font-terminal:  'VT323', 'Courier New', monospace;
--font-code:      'Fira Code', 'JetBrains Mono', monospace;
```

| Font | Usage |
|------|-------|
| JetBrains Mono | All numbers, scores, speeds, HUD labels |
| Rajdhani | Body text, labels, buttons |
| Orbitron | Display / title text (sign-in logo, hero headers) |
| VT323 | Terminal boot sequences, pseudo-code readouts |

**Phosphor Bleed & Aberration:** Apply subtle `text-shadow` layers to simulate glowing phosphor:
```css
text-shadow:
  0 0 4px rgba(0, 255, 102, 0.4),
  0 0 12px rgba(0, 255, 102, 0.15);
```

Chromatically-important text uses `::before`/`::after` for red/blue split:
```css
.chromatic-text::before { color: rgba(255, 0, 0, 0.4); transform: translateX(2px); }
.chromatic-text::after  { color: rgba(0, 80, 255, 0.4);  transform: translateX(-2px); }
```

## 2. CRT Screen Overlay — Global Execution

A permanent global overlay rendered over the entire viewport with `z-index: 9999` and `pointer-events: none`.

### 2.1 Scanlines

```css
.crt-scanlines {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9997;
  background: repeating-linear-gradient(
    to bottom,
    transparent 0px,
    transparent 2px,
    rgba(0, 255, 102, 0.035) 2px,
    rgba(0, 255, 102, 0.035) 4px
  );
}
```

Repeating horizontal linear gradients, 2px cycle, faint phosphor-tinted opacity.

### 2.2 Vignette

```css
.crt-vignette {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9996;
  background: radial-gradient(
    ellipse 70% 60% at 50% 50%,
    transparent 30%,
    rgba(0, 255, 102, 0.03) 50%,
    rgba(0, 0, 0, 0.6) 100%
  );
}
```

Simulates curved, darkened edges of a CRT tube monitor. Phosphor-tinted variant preferred.

### 2.3 Horizontal Retrace Line

```css
.crt-retrace {
  position: fixed;
  top: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--accent-green);
  opacity: 0;
  pointer-events: none;
  z-index: 9998;
  animation: crtRetrace 8s linear infinite;
}

@keyframes crtRetrace {
  0%   { top: -2px; opacity: 0; }
  0.1% { top: 0%;   opacity: 0.15; }
  0.2% { top: 2px;  opacity: 0; }
  100% { top: 100%; opacity: 0; }
}
```

---

## 3. Custom Imagery & Iconography

**No AI anomalies.** No FontAwesome, no Material Design, no standard icon packs.

### 3.1 Background Assets

- Style: Dark, monochromatic wireframes, tactical radar maps, server architectures, gritty cyberpunk cityscapes.
- Treatment: High contrast, heavily darkened, overlaid with `mix-blend-mode: screen` or `multiply` to blend with pure black and terminal green.

### 3.2 Bespoke Vector Icons

- Style: Sharp geometric SVG vectors — broken borders, crosshairs, radar sweeps, 80s/90s military blueprint aesthetic.
- States:
  - **Default:** Razor-sharp monochrome (green-on-black).
  - **Hover/Active:** Phosphor glow `drop-shadow(0 0 6px #00FF66)`.
  - **Click:** Sharp pulse + glitch/chromatic aberration animation (150ms translateX jitter + brief opacity flicker).

```css
.icon-glow {
  filter: drop-shadow(0 0 4px rgba(0, 255, 102, 0.4));
}

.icon-glitch:active {
  animation: iconGlitch 0.15s ease-out;
}

@keyframes iconGlitch {
  0%   { filter: drop-shadow(-2px 0 red) drop-shadow(2px 0 blue); transform: translateX(-2px); }
  50%  { transform: translateX(3px); }
  100% { filter: none; transform: translateX(0); }
}
```

---

## 4. Ambient Glitches & Interaction Animations

### 4.1 Random Micro-Glitches (Global Controller)

Trigger every 4–8 seconds. Targets UI containers for exactly 150ms.

```javascript
// Schedule random micro-glitches every 4–8 seconds
const glitchInterval = setInterval(() => {
  const targets = document.querySelectorAll('.glitch-target');
  const el = targets[Math.floor(Math.random() * targets.length)];
  if (el) {
    el.classList.add('glitch-active');
    setTimeout(() => el.classList.remove('glitch-active'), 150);
  }
}, 4000 + Math.random() * 4000);
```

```css
.glitch-active {
  opacity: 0.85;
  transform: translateX(3px) skewX(-0.5deg);
  clip-path: inset(10% 0 20% 0);
  transition: none;
}
```

**Visuals:** Fast opacity flicker (0.85), 2-3px horizontal positional jump (`translateX`), brief horizontal line tearing (`skewX` + `clip-path`).

### 4.2 Button Click — "System Execution"

Interactive buttons must feel violently responsive — like executing a mainframe command.

```css
.btn-execute {
  /* Default: transparent bg, green border */
  background: transparent;
  color: var(--accent-green);
  border: 1.5px solid var(--accent-green);
  transition: background 0.1s, color 0.1s, transform 0.06s;
}

.btn-execute:active {
  /* Instant inversion */
  background: var(--accent-green);
  color: #000000;
  transform: scale(0.97);
  box-shadow:
    0 0 12px rgba(0, 255, 102, 0.2),
    0 0 30px rgba(0, 255, 102, 0.06);
}
```

**Full click sequence:**
1. **Compress:** scale(0.97) in 60ms
2. **Invert:** bg → green, text → black
3. **Expand:** scale(1.02) then return(1.0) via spring easing
4. **Screen shake:** Global 200ms jitter on `body` (see below)

#### Screen Shake (Global)

```css
body.body-shake {
  animation: screenShake 0.2s ease-out;
}

@keyframes screenShake {
  0%   { transform: translate3d(0, 0, 0); }
  15%  { transform: translate3d(-3px, 1px, 0); }
  30%  { transform: translate3d(3px, -1px, 0); }
  45%  { transform: translate3d(-2px, 2px, 0); }
  60%  { transform: translate3d(2px, -2px, 0); }
  80%  { transform: translate3d(-1px, 0, 0); }
  100% { transform: translate3d(0, 0, 0); }
}
```

Simulates heavy terminal hardware processing.

---

## 5. Matrix Rain & Loading Mechanics

### 5.1 HTML5 Canvas Matrix Rain

Vertical streams of falling green alphanumeric / Katakana characters rendered via Canvas 2D API.

| Mode | Opacity | Use Case |
|------|---------|----------|
| Idle | `0.05–0.1` | Standard app screens (below text readability threshold) |
| Loading | `0.6–0.8` | Boot, data sync, heavy processing |

```javascript
class MatrixRain {
  constructor(canvas, opts = {}) {
    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');
    this.fontSize = opts.fontSize || 14;
    this.columns = Math.floor(canvas.width / this.fontSize);
    this.drops = Array(this.columns).fill(1);
    this.chars = 'アイウエオカキクケコサシスセソタチツテトナニヌネノ0123456789ABCDEF';
    this.opacity = opts.opacity || 0.08;
  }

  draw() {
    this.ctx.fillStyle = `rgba(0, 0, 0, ${0.05})`;
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
    this.ctx.fillStyle = `rgba(0, 255, 102, ${this.opacity})`;
    this.ctx.font = `${this.fontSize}px monospace`;

    for (let i = 0; i < this.drops.length; i++) {
      const char = this.chars[Math.floor(Math.random() * this.chars.length)];
      this.ctx.fillText(char, i * this.fontSize, this.drops[i] * this.fontSize);
      if (this.drops[i] * this.fontSize > this.canvas.height && Math.random() > 0.975) {
        this.drops[i] = 0;
      }
      this.drops[i]++;
    }
  }
}
```

### 5.2 Hacker-Style Data Loading

No smooth spinners. Erratic, chaotic progress bars with terminal readouts.

```css
.loading-bar {
  width: 100%;
  height: 6px;
  background: var(--phosphor-track); /* #003D1A */
  position: relative;
  overflow: hidden;
}

.loading-bar__fill {
  height: 100%;
  width: 0%;
  background: var(--accent-green);
  box-shadow: 0 0 8px rgba(0, 255, 102, 0.4);
  animation: erraticProgress 4s ease-in-out forwards;
}

@keyframes erraticProgress {
  0%   { width: 0%; }
  20%  { width: 25%; }
  25%  { width: 20%; }    /* jumps back 5% */
  50%  { width: 45%; }
  55%  { width: 40%; }    /* stalls and regresses */
  75%  { width: 75%; }
  80%  { width: 70%; }    /* stalls again */
  95%  { width: 90%; }
  100% { width: 100%; }
}
```

**Terminal Readouts:** Display rapid alternating pseudo-code next to the loader:
```
> ESTABLISHING SECURE CONNECTION... OK
> DECRYPTING DATA PACKETS... [RETRY]
> HANDSHAKE: PROTOCOL v2.0... OK
> SYNCHRONIZING RIDE DATABASE... [FAIL]
> RETRYING... OK
> SYSTEM READY.
```

### 5.3 Typewriter Text Execution

Text elements mount character-by-character with a solid blinking block cursor `█`.

```javascript
class Typewriter {
  constructor(el, text, speed = 40) {
    this.el = el;
    this.text = text;
    this.speed = speed;
    this.cursor = document.createElement('span');
    this.cursor.className = 'cursor-blink cursor-blink--block';
    this.el.appendChild(this.cursor);
    this.i = 0;
  }

  start() {
    const tick = () => {
      if (this.i < this.text.length) {
        this.cursor.before(this.text[this.i]);    // type char before cursor
        this.i++;
        setTimeout(tick, this.speed + Math.random() * 20);
      } else {
        this.cursor.remove();      // cursor deletes itself upon completion
      }
    };
    tick();
  }
}
```

```css
.cursor-blink--block {
  display: inline-block;
  width: 8px;
  height: 1em;
  background: var(--accent-green);
  animation: cursorBlink 0.8s steps(1) infinite;
  vertical-align: text-bottom;
}

@keyframes cursorBlink {
  0%, 100% { opacity: 1; }
  50%      { opacity: 0; }
}
```

---

## 6. Comprehensive Audio Design

Centralized `AudioManager` using Web Audio API. Visible hardware-style toggle switch must mute/unmute all audio.

### 6.1 AudioManager Implementation

```javascript
class AudioManager {
  constructor() {
    this.ctx = null;           // AudioContext (created on first user gesture)
    this.muted = false;        // global mute state
    this.masterGain = null;
  }

  /** Create AudioContext on first user interaction (browser policy) */
  ensureContext() {
    if (this.ctx) return;
    this.ctx = new (window.AudioContext || window.webkitAudioContext)();
    this.masterGain = this.ctx.createGain();
    this.masterGain.gain.value = 0.5;
    this.masterGain.connect(this.ctx.destination);
  }

  /** Toggle mute state */
  toggleMute() {
    this.muted = !this.muted;
    if (this.masterGain) {
      this.masterGain.gain.value = this.muted ? 0 : 0.5;
    }
  }

  /** Generate oscillator tone */
  playTone(freq, duration, type = 'sine', volume = 0.1) {
    if (this.muted || !this.ctx) return;
    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();
    osc.type = type;
    osc.frequency.setValueAtTime(freq, this.ctx.currentTime);
    gain.gain.setValueAtTime(volume, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + duration);
    osc.connect(gain);
    gain.connect(this.masterGain);
    osc.start();
    osc.stop(this.ctx.currentTime + duration);
  }

  /** Generate white noise burst (for glitch SFX) */
  playNoise(duration, volume = 0.05) {
    if (this.muted || !this.ctx) return;
    const bufferSize = this.ctx.sampleRate * duration;
    const buffer = this.ctx.createBuffer(1, bufferSize, this.ctx.sampleRate);
    const data = buffer.getChannelData(0);
    for (let i = 0; i < bufferSize; i++) data[i] = (Math.random() * 2 - 1);
    const src = this.ctx.createBufferSource();
    src.buffer = buffer;
    const gain = this.ctx.createGain();
    gain.gain.setValueAtTime(volume, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + duration);
    src.connect(gain);
    gain.connect(this.masterGain);
    src.start();
  }
}

const Audio = new AudioManager();
```

### 6.2 Audio Cue Specifications

| Cue | Type | Trigger | Implementation |
|-----|------|---------|---------------|
| Ambient drone | Continuous oscillator | Page load (visible toggle) | Two detuned sine waves (55Hz + 87Hz), very low gain (0.02–0.05) |
| Text typewriter click | Oscillator burst | Each character in typewriter | 800Hz sine, 25ms duration, gain 0.08 |
| Button hover | Oscillator sweep | `mouseenter` | 600Hz→900Hz sine, 50ms, gain 0.06 |
| Button click / execution | Oscillator + noise | `mousedown` | 100Hz + 200Hz square, 150ms, gain 0.15 |
| Glitch SFX | White noise burst | Random micro-glitch trigger | 50ms noise, gain 0.05, + brief 40Hz sine pulse |
| Rank up fanfare | Oscillator sequence | Rank/badge unlock | 262Hz→349Hz→440Hz→523Hz (C4→F4→A4→C5), 200ms each, sine |

### 6.3 Visible Audio Toggle

Hardware-style switch, not a software checkbox:

```html
<div class="audio-toggle" id="audio-toggle" role="switch" aria-checked="true" tabindex="0">
  <span class="audio-toggle__label">AUDIO</span>
  <span class="audio-toggle__track">
    <span class="audio-toggle__thumb"></span>
  </span>
</div>
```

```css
.audio-toggle {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  user-select: none;
}

.audio-toggle__track {
  width: 28px;
  height: 12px;
  border: 1px solid var(--accent-green);
  border-radius: 6px;
  position: relative;
  background: transparent;
  transition: background 0.2s;
}

.audio-toggle__thumb {
  position: absolute;
  top: 1px;
  left: 1px;
  width: 8px;
  height: 8px;
  background: var(--accent-green);
  border-radius: 50%;
  transition: left 0.2s;
}

.audio-toggle[aria-checked="false"] .audio-toggle__thumb { left: 17px; }
.audio-toggle[aria-checked="false"] .audio-toggle__track { background: var(--phosphor-dark); }
```

---

## 7. Border Radii & Spacing

| Token | Value | Usage |
|-------|-------|-------|
| `--radius-sm` | 8dp | Cards |
| `--radius-md` | 14dp | Modals |
| `--radius-lg` | 22dp | Rank badges |
| `--radius-pill` | 999dp | All buttons |
| `--sp-1` | 4px | Grid base unit |

### Grid

- 4px baseline grid. All spacing in multiples of 4: 8, 12, 16, 24, 32, 40, 48, 64px.

---

## 8. State Patterns

### 8.1 Button States

| State | Background | Border | Text | Glow |
|-------|-----------|--------|------|------|
| Default | Transparent | `1.5px solid #00FF66` | `#00FF66` | None |
| Hover | `#00FF66` | `#00FF66` | `#000000` | `0 0 12px rgba(0,255,102,0.2)` |
| Active | `#00FF66` | `#00FF66` | `#000000` | Scale(0.97) |
| Executing | `#000000` | `#00FF66` | `#00FF66` | Glitch animation |
| Disabled | Transparent | `#003D1A` | `#005522` | None |

### 8.2 Card States

| State | Fill | Border | Text |
|-------|------|--------|------|
| Default | `#121212` | `1px solid rgba(255,255,255,0.07)` | Standard |
| Hover | `#1A1A1A` | `1px solid rgba(0,255,102,0.25)` | Brightened |
| Active | `#222222` | `1px solid rgba(0,255,102,0.40)` | Full brightness |
| Empty | `#0A0A0A` | `1px dashed #333` | `#555555` text |
| Error | `#1A0A0A` | `1px solid #FF453A` | `#FF453A` text |

### 8.3 Loading States

| State | Visual | Description |
|-------|--------|-------------|
| Initial | Boot sequence | Full-screen terminal boot overlay with Matrix rain |
| Loading | Skeleton shimmer | Animated gradient sweep on placeholder blocks |
| Refreshing | Erratic progress bar | Chaotic fill + terminal readout text |
| Stream | Typewriter reveal | Characters appear one-by-one with cursor |

### 8.4 Empty States

| Context | Icon | Headline | Body |
|---------|------|----------|------|
| No trips | Radar dish with no signal | `NO TRIPS RECORDED` | System awaits first ride data |
| No badges | Trophy outline empty | `NO BADGES EARNED` | Complete challenges to unlock |
| No friends | Crosshair no target | `NO FRIENDS FOUND` | Refer or search by username |
| No data | Terminal cursor blinking | `NO DATA AVAILABLE` | Check sensors / connectivity |

---

## 9. Motion & Transition Tokens

```css
:root {
  --ease-out-expo:   cubic-bezier(0.16, 1, 0.3, 1);
  --ease-in-out:     cubic-bezier(0.65, 0, 0.35, 1);
  --ease-spring:     cubic-bezier(0.34, 1.56, 0.64, 1);
  --ease-glitch:     cubic-bezier(0.25, 0.46, 0.45, 0.94);
  --duration-fast:   150ms;
  --duration-med:    300ms;
  --duration-slow:   600ms;
}
```

| Transition | Easing | Duration | Example |
|-----------|--------|----------|---------|
| Hover (buttons) | ease-out-expo | 150ms | Background color, border glow |
| Active (buttons) | linear | 60ms | Scale down |
| Return (buttons) | spring | 200ms | Scale return |
| Navigation | ease-in-out | 300ms | Slide transition |
| Glitch | glitch | 150ms | TranslateX, opacity |
| Count-up (numbers) | ease-out-expo | 600ms | Score reveal, rank progress |
| Fade-in (scan) | linear | 400–800ms | Data entry scan effect |
| Loading bar | linear | 4s | Erratic progress fill |

---

## 10. Implementation Priority

### Phase 1 — Foundation (Required for all screens)
- [ ] CSS custom properties (design tokens)
- [ ] Global CRT scanline overlay
- [ ] Global vignette overlay
- [ ] Font imports (Fira Code, JetBrains Mono, Orbitron, Rajdhani, VT323)

### Phase 2 — Core Interactions
- [ ] Button system (pill shape, inversion, execution animation)
- [ ] Screen shake on primary actions
- [ ] Random micro-glitch controller
- [ ] Typewriter text component

### Phase 3 — Visual Depth
- [ ] Canvas Matrix Rain (idle mode)
- [ ] HUD background mesh (radar grid overlay)
- [ ] Chromatic aberration on display text
- [ ] Phosphor glow via text-shadow / drop-shadow

### Phase 4 — Loading & Feedback
- [ ] Boot sequence overlay (full-screen terminal)
- [ ] Erratic loading bar
- [ ] Skeleton shimmer components
- [ ] Terminal readout messages

### Phase 5 — Audio
- [ ] AudioManager initialization on first gesture
- [ ] Ambient drone oscillator
- [ ] Typewriter click per character
- [ ] Button hover blip
- [ ] Button execution chime
- [ ] Glitch SFX
- [ ] Rank-up fanfare
- [ ] Audio toggle switch (hardware-style, visible, functional)

---

## 11. File Structure (Reference Implementation)

```
styles/
  revrank-theme.css         — Design tokens, CRT overlays, glitch keyframes,
                              typewriter, button system, bottom nav, loading bar

scripts/
  revrank-theme.js          — AudioManager, MatrixRain, Typewriter,
                              GlitchScheduler, LoadSimulator, boot()

designs/
  b00-sign-in-google.html   — Sign-in with motorcycle hero + CRT overlays
  b00-slide-1-welcome.html  — Onboarding: welcome slide + Matrix Rain
  b00-slide-2-how-you-drive.html
  b00-vehicle-type.html
  b00-username-picker.html
  b02-live-hud-tracking.html
  b02-permissions-gps-bt.html
  ...
```

---

## 12. Non-Negotiables

1. **Dark mode only.** No light theme. Background is `#000000`.
2. **All buttons are pill-shaped** (`--radius-pill: 999px`), no exceptions.
3. **All cards are filled** (`#121212`) — no outline-only cards.
4. **Paywall never before 3 trips** — enforced by `PaywallTriggerManager`.
5. **Every changing number animates** — count-up or fill-in effect.
6. **No AI slop** — no generic gradients, no default icon packs, no font pairings that look like every other AI-designed site.
7. **No modern smooth spinners** — only erratic progress bars and terminal readouts.
8. **Audio is optional** — user must be able to mute everything via the toggle switch.
