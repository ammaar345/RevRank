/* ===================================================================
   revrank-theme.js — RevRank Runtime Engine
   Matrix Rain Canvas + Web Audio API Sound Design + Utilities

   🔗  Referenced by revrank-theme.css and every screen HTML.
      Load after the CSS import, before the screen body closes.

   Exports (window):
     - RevRank.MatrixRain      — Canvas-based Matrix code rain
     - RevRank.Audio           — Web Audio API sound engine
     - RevRank.Typewriter      — Typewriter text effect
     - RevRank.GlitchScheduler  — Ambient micro-glitch manager
     - RevRank.LoadSimulator   — Glitchy data-load progress
   =================================================================== */

window.RevRank = window.RevRank || {};


/* ===================================================================
   MATRIX RAIN
   HTML5 Canvas vertical code streams with configurable density/speed.
   =================================================================== */
RevRank.MatrixRain = class MatrixRain {
  constructor(container, opts = {}) {
    this.container = typeof container === 'string'
      ? document.querySelector(container)
      : container;
    if (!this.container) throw new Error('MatrixRain: container not found');

    this.opts = {
      density:        opts.density       ?? 0.65,
      speed:          opts.speed         ?? 1.0,
      charBrightness: opts.charBrightness ?? 0.85,
      fontSize:       opts.fontSize      ?? 14,
      color:          opts.color         ?? '#00FF66',
      headColor:      opts.headColor     ?? '#AAFFCC',
      fadeColor:      opts.fadeColor     ?? 'rgba(0,255,102,0.05)',
      tailLength:     opts.tailLength    ?? 12,
      minSpeed:       opts.minSpeed      ?? 0.3,
      maxSpeed:       opts.maxSpeed      ?? 2.5,
      idleOpacity:    opts.idleOpacity   ?? 0.35,
      loadingOpacity: opts.loadingOpacity ?? 0.95,
      characterSet:   opts.characterSet  ?? null, // default: katakana + latin
    };

    this.canvas = null;
    this.ctx = null;
    this.columns = [];
    this.drops = [];
    this.animationId = null;
    this.isRunning = false;
    this.isLoading = false;
    this.resizeHandler = null;

    this._init();
  }

  _init() {
    this.canvas = document.createElement('canvas');
    this.canvas.style.display = 'block';
    this.canvas.style.width = '100%';
    this.canvas.style.height = '100%';
    this.canvas.style.transition = 'opacity 0.8s ease';
    this.canvas.style.opacity = this.opts.idleOpacity;
    this.container.appendChild(this.canvas);
    this.ctx = this.canvas.getContext('2d');

    this._onResize();
    this.resizeHandler = () => this._onResize();
    window.addEventListener('resize', this.resizeHandler);

    this._initDrops();
  }

  _onResize() {
    const rect = this.container.getBoundingClientRect();
    const dpr = window.devicePixelRatio || 1;
    this.canvas.width = rect.width * dpr;
    this.canvas.height = rect.height * dpr;
    this.ctx.scale(dpr, dpr);
    this.width = rect.width;
    this.height = rect.height;
    this.colWidth = this.opts.fontSize * 1.15;
    this.colCount = Math.floor(this.width / this.colWidth);
  }

  _chars() {
    if (this.opts.characterSet) return this.opts.characterSet;
    // Default: katakana + latin + numbers + symbols
    return 'アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン' +
           '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ' +
           '{}[]<>()/\\|`~!@#$%^&*-_=+:;,' +
           'ТРЕВОГА';
  }

  _initDrops() {
    this.drops = [];
    const chars = this._chars();
    const count = Math.floor(this.colCount * this.opts.density);
    const occupied = new Set();

    for (let i = 0; i < count; i++) {
      let col;
      let attempts = 0;
      do {
        col = Math.floor(Math.random() * this.colCount);
        attempts++;
      } while (occupied.has(col) && attempts < 50);
      occupied.add(col);

      this.drops.push({
        col,
        y: Math.random() * this.height * -1,
        speed: this.opts.minSpeed + Math.random() * (this.opts.maxSpeed - this.opts.minSpeed) * this.opts.speed,
        length: 5 + Math.floor(Math.random() * this.opts.tailLength),
        chars: Array.from({ length: 30 }, () => chars[Math.floor(Math.random() * chars.length)]),
        charIndex: 0,
        flicker: Math.random() * 0.3,
        hueShift: Math.random() * 15 - 7, // slight per-column color variation
      });
    }
  }

  _drawDrop(drop) {
    const ctx = this.ctx;
    const fs = this.opts.fontSize;
    const x = drop.col * this.colWidth;
    const speed = drop.speed;
    const len = drop.length;
    const brightness = this.opts.charBrightness;

    for (let i = 0; i < len; i++) {
      const cy = drop.y - i * fs;
      if (cy < -fs || cy > this.height + fs) continue;

      // Head character (leading edge) — brightest
      if (i === 0) {
        ctx.fillStyle = this.opts.headColor;
        ctx.shadowBlur = 15;
        ctx.shadowColor = this.opts.color;
      } else {
        // Tail — fades out
        const tailAlpha = Math.max(0, 1 - (i / len) * 1.2) * brightness * 0.9;
        ctx.shadowBlur = i < 3 ? 10 : 0;
        ctx.shadowColor = 'transparent';
        ctx.fillStyle = `rgba(0, 255, ${102 + Math.floor(drop.hueShift)}, ${tailAlpha})`;
      }

      const char = drop.chars[(drop.charIndex - i + drop.chars.length) % drop.chars.length];

      // Random flicker
      if (Math.random() < drop.flicker * 0.01) continue;

      ctx.font = `${fs}px "JetBrains Mono", "Fira Code", monospace`;
      ctx.fillText(char, x, cy);
    }

    ctx.shadowBlur = 0;
  }

  _tick() {
    const ctx = this.ctx;
    const w = this.width;
    const h = this.height;

    // Fade trail (not clear — creates persistence trail effect)
    ctx.fillStyle = this.opts.fadeColor;
    ctx.fillRect(0, 0, w, h);

    // Draw each column
    for (const drop of this.drops) {
      this._drawDrop(drop);

      drop.y += drop.speed * this.opts.speed;
      drop.charIndex++;

      // Random speed changes for organic feel
      if (Math.random() < 0.005) {
        drop.speed = this.opts.minSpeed + Math.random() * (this.opts.maxSpeed - this.opts.minSpeed) * this.opts.speed;
      }

      // Reset when off-screen (with random gap for natural look)
      if (drop.y - drop.length * this.opts.fontSize > h + 50) {
        drop.y = -Math.random() * h * 0.3;
        drop.speed = this.opts.minSpeed + Math.random() * (this.opts.maxSpeed - this.opts.minSpeed) * this.opts.speed;
        drop.col = Math.floor(Math.random() * this.colCount);
        // Refresh chars
        const chars = this._chars();
        drop.chars = Array.from({ length: 30 }, () => chars[Math.floor(Math.random() * chars.length)]);
        drop.charIndex = 0;
      }
    }
  }

  start() {
    if (this.isRunning) return;
    this.isRunning = true;

    const loop = () => {
      if (!this.isRunning) return;
      this._tick();
      this.animationId = requestAnimationFrame(loop);
    };
    loop();
  }

  stop() {
    this.isRunning = false;
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
      this.animationId = null;
    }
  }

  setLoading(loading) {
    this.isLoading = loading;
    this.canvas.style.opacity = loading
      ? this.opts.loadingOpacity
      : this.opts.idleOpacity;
  }

  setDensity(density) {
    this.opts.density = Math.max(0.1, Math.min(1, density));
    this._initDrops();
  }

  setSpeed(speed) {
    this.opts.speed = Math.max(0.25, Math.min(3, speed));
  }

  destroy() {
    this.stop();
    if (this.resizeHandler) {
      window.removeEventListener('resize', this.resizeHandler);
    }
    if (this.canvas && this.container.contains(this.canvas)) {
      this.container.removeChild(this.canvas);
    }
  }
};


/* ===================================================================
   WEB AUDIO API — Sound Design Engine

   Features:
     - Ambient sub-hum (sine 55Hz + 87Hz, LFO modulated)
     - Text generation clicks (filtered noise bursts)
     - Button hover blip (sine frequency sweep)
     - Button execution metallic chime (C5/E5/G5 chord)
     - Glitch SFX (noise burst with pitch drop)
     - Rank-up fanfare / badge unlock (ascending arp)
   =================================================================== */
RevRank.Audio = class AudioEngine {
  constructor(opts = {}) {
    this.enabled = opts.enabled ?? true;
    this.volume = opts.volume ?? 0.5;
    this.ctx = null;
    this.initialized = false;
    this.masterGain = null;
    this.humGain = null;
    this.humOsc1 = null;
    this.humOsc2 = null;
    this.humLfo = null;
    this.analyser = null;
  }

  async init() {
    if (this.initialized) return;
    try {
      this.ctx = new (window.AudioContext || window.webkitAudioContext)();
      this.masterGain = this.ctx.createGain();
      this.masterGain.gain.value = this.volume * 0.5;
      this.masterGain.connect(this.ctx.destination);

      // Analyser for potential visualizations
      this.analyser = this.ctx.createAnalyser();
      this.analyser.fftSize = 256;
      this.masterGain.connect(this.analyser);

      this.initialized = true;
      document.documentElement.classList.add('audio-ready');
    } catch (e) {
      console.warn('AudioEngine: init failed', e);
      this.enabled = false;
    }
  }

  ensureResumed() {
    if (this.ctx && this.ctx.state === 'suspended') {
      this.ctx.resume();
    }
  }

  setVolume(v) {
    this.volume = Math.max(0, Math.min(1, v));
    if (this.masterGain) {
      this.masterGain.gain.value = this.volume * 0.5;
    }
  }

  /* --- Ambient hum (sub-bass + fifth, LFO modulated) --- */
  startHum() {
    if (!this.initialized || !this.enabled) return;

    this.humGain = this.ctx.createGain();
    this.humGain.gain.value = 0.025;
    this.humGain.connect(this.masterGain);

    // Sub: 55Hz sine
    this.humOsc1 = this.ctx.createOscillator();
    this.humOsc1.type = 'sine';
    this.humOsc1.frequency.value = 55;
    this.humOsc1.connect(this.humGain);

    // Fifth: 87Hz (55 * 1.618)
    this.humOsc2 = this.ctx.createOscillator();
    this.humOsc2.type = 'sine';
    this.humOsc2.frequency.value = 87;
    this.humOsc2.connect(this.humGain);

    // LFO for subtle movement (0.3Hz)
    this.humLfo = this.ctx.createOscillator();
    this.humLfo.type = 'sine';
    this.humLfo.frequency.value = 0.3;
    const lfoGain = this.ctx.createGain();
    lfoGain.gain.value = 0.008;
    this.humLfo.connect(lfoGain);
    lfoGain.connect(this.humGain.gain);

    this.humOsc1.start();
    this.humOsc2.start();
    this.humLfo.start();
  }

  stopHum() {
    try {
      if (this.humOsc1) { this.humOsc1.stop(); this.humOsc1 = null; }
      if (this.humOsc2) { this.humOsc2.stop(); this.humOsc2 = null; }
      if (this.humLfo) { this.humLfo.stop(); this.humLfo = null; }
    } catch (e) { /* ignore stop errors */ }
  }

  /* --- Text generation click (filtered noise burst) --- */
  textClick() {
    if (!this.initialized || !this.enabled) return;
    this.ensureResumed();

    const dur = 0.008 + Math.random() * 0.015;
    const bufferSize = Math.floor(this.ctx.sampleRate * dur);
    const buffer = this.ctx.createBuffer(1, bufferSize, this.ctx.sampleRate);
    const data = buffer.getChannelData(0);
    for (let i = 0; i < bufferSize; i++) {
      data[i] = (Math.random() * 2 - 1) * Math.pow(1 - i / bufferSize, 2);
    }

    const source = this.ctx.createBufferSource();
    source.buffer = buffer;

    // Bandpass filter (2kHz–4kHz)
    const filter = this.ctx.createBiquadFilter();
    filter.type = 'bandpass';
    filter.frequency.value = 1500 + Math.random() * 2500;
    filter.Q.value = 1.5;

    const gain = this.ctx.createGain();
    gain.gain.value = 0.015 + Math.random() * 0.02;

    source.connect(filter);
    filter.connect(gain);
    gain.connect(this.masterGain);
    source.start();
  }

  /* --- Button hover blip (sine frequency sweep 1200→600Hz, 60ms) --- */
  hoverBlip() {
    if (!this.initialized || !this.enabled) return;
    this.ensureResumed();

    const osc = this.ctx.createOscillator();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(1200, this.ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(600, this.ctx.currentTime + 0.06);

    const gain = this.ctx.createGain();
    gain.gain.setValueAtTime(0.03, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + 0.06);

    osc.connect(gain);
    gain.connect(this.masterGain);
    osc.start(this.ctx.currentTime);
    osc.stop(this.ctx.currentTime + 0.06);
  }

  /* --- Button execution metallic chime (C5+E5+G5 chord, 300ms) --- */
  executionChime() {
    if (!this.initialized || !this.enabled) return;
    this.ensureResumed();

    const freqs = [523.25, 659.25, 783.99]; // C5, E5, G5
    const now = this.ctx.currentTime;

    // Noise attack (5ms click)
    const noiseDur = 0.005;
    const noiseBuf = this.ctx.createBuffer(1, Math.floor(this.ctx.sampleRate * noiseDur), this.ctx.sampleRate);
    const noiseData = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseData.length; i++) {
      noiseData[i] = (Math.random() * 2 - 1) * 0.3;
    }
    const noiseSrc = this.ctx.createBufferSource();
    noiseSrc.buffer = noiseBuf;
    const noiseGain = this.ctx.createGain();
    noiseGain.gain.setValueAtTime(0.08, now);
    noiseGain.gain.exponentialRampToValueAtTime(0.001, now + 0.01);
    noiseSrc.connect(noiseGain);
    noiseGain.connect(this.masterGain);
    noiseSrc.start(now);

    // Chord
    for (const freq of freqs) {
      const osc = this.ctx.createOscillator();
      osc.type = 'sine';
      osc.frequency.value = freq;

      const gain = this.ctx.createGain();
      gain.gain.setValueAtTime(0.06, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.3);

      osc.connect(gain);
      gain.connect(this.masterGain);
      osc.start(now);
      osc.stop(now + 0.3);
    }
  }

  /* --- Glitch SFX (noise burst with rapid pitch drop, 50ms) --- */
  glitchSFX(intensity = 0.5) {
    if (!this.initialized || !this.enabled) return;
    this.ensureResumed();

    const dur = 0.015 + intensity * 0.035;
    const now = this.ctx.currentTime;

    // Noise burst
    const buf = this.ctx.createBuffer(1, Math.floor(this.ctx.sampleRate * dur), this.ctx.sampleRate);
    const data = buf.getChannelData(0);
    for (let i = 0; i < data.length; i++) {
      data[i] = (Math.random() * 2 - 1) * Math.pow(1 - i / data.length, 0.5);
    }
    const src = this.ctx.createBufferSource();
    src.buffer = buf;
    src.playbackRate.setValueAtTime(1, now);
    src.playbackRate.exponentialRampToValueAtTime(0.3, now + dur);

    const gain = this.ctx.createGain();
    gain.gain.setValueAtTime(0.06 + intensity * 0.1, now);
    gain.gain.exponentialRampToValueAtTime(0.001, now + dur + 0.02);

    const filter = this.ctx.createBiquadFilter();
    filter.type = 'highpass';
    filter.frequency.value = 500 - intensity * 300;

    src.connect(filter);
    filter.connect(gain);
    gain.connect(this.masterGain);
    src.start(now);
  }

  /* --- Rank-up fanfare (ascending arp, G4→B4→D5→G5) --- */
  fanfare() {
    if (!this.initialized || !this.enabled) return;
    this.ensureResumed();

    const notes = [
      { freq: 392.00, time: 0.00 },  // G4
      { freq: 493.88, time: 0.12 },  // B4
      { freq: 587.33, time: 0.24 },  // D5
      { freq: 783.99, time: 0.36 },  // G5
    ];
    const now = this.ctx.currentTime;

    for (const note of notes) {
      const osc = this.ctx.createOscillator();
      osc.type = 'sine';
      osc.frequency.value = note.freq;

      const gain = this.ctx.createGain();
      gain.gain.setValueAtTime(0, now + note.time);
      gain.gain.linearRampToValueAtTime(0.08, now + note.time + 0.02);
      gain.gain.exponentialRampToValueAtTime(0.001, now + note.time + 0.25);

      osc.connect(gain);
      gain.connect(this.masterGain);
      osc.start(now + note.time);
      osc.stop(now + note.time + 0.25);
    }
  }

  destroy() {
    this.stopHum();
    if (this.ctx && this.ctx.state !== 'closed') {
      this.ctx.close();
    }
    this.initialized = false;
  }
};


/* ===================================================================
   TYPEWRITER — Character-by-character text reveal
   =================================================================== */
RevRank.Typewriter = class Typewriter {
  constructor(element, opts = {}) {
    this.el = typeof element === 'string' ? document.querySelector(element) : element;
    if (!this.el) throw new Error('Typewriter: element not found');

    this.opts = {
      text:        opts.text        || this.el.textContent || '',
      speed:       opts.speed       || 45,     // ms per char
      cursor:      opts.cursor      ?? true,
      cursorChar:  opts.cursorChar  || '█',
      cursorBlink: opts.cursorBlink ?? true,
      startDelay:  opts.startDelay  || 200,
      onChar:      opts.onChar      || null,
      onComplete:  opts.onComplete  || null,
    };

    this.position = 0;
    this.timer = null;
    this.isRunning = false;
    this.cursorEl = null;

    this.el.innerHTML = '';
    if (this.opts.cursor) {
      this.cursorEl = document.createElement('span');
      this.cursorEl.className = 'typewriter-cursor';
      this.cursorEl.textContent = this.opts.cursorChar;
      this.cursorEl.style.animation = this.opts.cursorBlink
        ? 'cursorBlink 0.8s steps(1) infinite'
        : 'none';
      this.cursorEl.style.color = 'var(--phosphor, #00FF66)';
      this.cursorEl.style.marginLeft = '2px';
    }
  }

  start() {
    if (this.isRunning) return;
    this.isRunning = true;
    this.position = 0;
    this.el.textContent = '';

    if (this.cursorEl) this.el.appendChild(this.cursorEl);

    const text = this.opts.text;
    const speed = this.opts.speed;
    const audio = window.RevRank?.Audio ? window.RevRank.audioInstance : null;

    setTimeout(() => {
      this.timer = setInterval(() => {
        if (this.position >= text.length) {
          clearInterval(this.timer);
          this.timer = null;
          this.isRunning = false;
          if (this.opts.onComplete) this.opts.onComplete();
          return;
        }

        const char = text[this.position];
        const isSpace = char === ' ';

        if (!isSpace && audio && Math.random() < 0.6) {
          audio.textClick();
        }

        if (this.cursorEl) {
          this.cursorEl.remove();
        }

        this.el.textContent += char;

        if (this.cursorEl) {
          this.el.appendChild(this.cursorEl);
        }

        if (this.opts.onChar) this.opts.onChar(this.position, char);

        this.position++;

        // Variable speed for realism
        if (Math.random() < 0.15) {
          clearInterval(this.timer);
          const pause = speed * (0.5 + Math.random() * 2);
          this.timer = setInterval(arguments.callee, speed * (0.5 + Math.random() * 0.5));
          // Use adaptive timing instead
          clearInterval(this.timer);
          const newSpeed = speed * (0.5 + Math.random() * 1.0);
          this.timer = setInterval(() => {
            if (this.position >= text.length) { /* same as above */ }
            /* Re-enter loop */
            this._tick();
          }, newSpeed);
        }
      }, speed);
    }, this.opts.startDelay);
  }

  _tick() {
    if (this.position >= this.opts.text.length) {
      clearInterval(this.timer);
      this.timer = null;
      this.isRunning = false;
      if (this.opts.onComplete) this.opts.onComplete();
      return;
    }

    const char = this.opts.text[this.position];
    const audio = window.RevRank?.audioInstance;

    if (char !== ' ' && audio && Math.random() < 0.6) {
      audio.textClick();
    }

    if (this.cursorEl) this.cursorEl.remove();
    this.el.textContent += char;
    if (this.cursorEl) this.el.appendChild(this.cursorEl);

    if (this.opts.onChar) this.opts.onChar(this.position, char);
    this.position++;
  }

  stop() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
    this.isRunning = false;
  }

  reset() {
    this.stop();
    this.position = 0;
    this.el.textContent = '';
    if (this.cursorEl) this.el.appendChild(this.cursorEl);
  }

  destroy() {
    this.stop();
    if (this.cursorEl) this.cursorEl.remove();
  }
};


/* ===================================================================
   GLITCH SCHEDULER — Ambient micro-glitches every 4-8s
   =================================================================== */
RevRank.GlitchScheduler = class GlitchScheduler {
  constructor(targets, opts = {}) {
    this.targets = [];
    if (typeof targets === 'string') {
      document.querySelectorAll(targets).forEach(el => this.targets.push(el));
    } else if (targets instanceof Element) {
      this.targets = [targets];
    } else if (Array.isArray(targets)) {
      this.targets = targets;
    }

    this.opts = {
      minInterval: opts.minInterval ?? 4000,
      maxInterval: opts.maxInterval ?? 8000,
      glitchClass: opts.glitchClass || 'glitch-triggered',
      variant:     opts.variant     || 'micro',  // micro | vertical | flicker | hshift | random
      audio:       opts.audio       ?? true,
      intensity:   opts.intensity   ?? 0.4,
    };

    this.timer = null;
    this.isRunning = false;

    // Possible glitch animations
    this._glitchVariants = [
      'glitchMicro',
      'glitchVertical',
      'glitchFlicker',
      'glitchHShift',
    ];
  }

  start() {
    if (this.isRunning) return;
    this.isRunning = true;
    this._schedule();
  }

  _schedule() {
    if (!this.isRunning) return;
    const delay = this.opts.minInterval + Math.random() * (this.opts.maxInterval - this.opts.minInterval);

    this.timer = setTimeout(() => {
      if (!this.isRunning) return;
      this._glitch();
      this._schedule();
    }, delay);
  }

  _glitch() {
    if (this.targets.length === 0) return;

    // Pick random target
    const target = this.targets[Math.floor(Math.random() * this.targets.length)];

    // Pick variant
    let animName;
    if (this.opts.variant === 'random') {
      animName = this._glitchVariants[Math.floor(Math.random() * this._glitchVariants.length)];
    } else {
      animName = 'glitch' + this.opts.variant.charAt(0).toUpperCase() + this.opts.variant.slice(1);
      // Map friendly names
      animName = animName.replace('Glitchmicro', 'GlitchMicro')
                         .replace('Glitchvertical', 'GlitchVertical')
                         .replace('Glitchflicker', 'GlitchFlicker')
                         .replace('Glitchhshift', 'GlitchHShift');
    }

    // Remove previous animation
    target.style.animation = 'none';
    // Force reflow
    void target.offsetWidth;

    // Apply glitch
    target.style.animation = `${animName} 0.15s cubic-bezier(0.25, 0.46, 0.45, 0.94) 1`;

    // Play glitch SFX
    const audio = window.RevRank?.audioInstance;
    if (this.opts.audio && audio) {
      audio.glitchSFX(this.opts.intensity);
    }

    // Clean up
    setTimeout(() => {
      target.style.animation = '';
    }, 200);
  }

  stop() {
    this.isRunning = false;
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = null;
    }
  }

  triggerNow() {
    this._glitch();
  }

  destroy() {
    this.stop();
    this.targets = [];
  }
};


/* ===================================================================
   LOAD SIMULATOR — Glitchy data-load progress with pseudo-code strings
   =================================================================== */
RevRank.LoadSimulator = class LoadSimulator {
  constructor(opts = {}) {
    this.opts = {
      duration:     opts.duration     || 2500,
      barSelector:  opts.barSelector  || '.loading-bar__fill',
      statusSelector: opts.statusSelector || '.loading-status',
      onComplete:   opts.onComplete   || null,
      audio:        opts.audio        ?? true,
      steps:        opts.steps        || [
        { pct: 12, msg: 'scsi device scan...' },
        { pct: 22, msg: 'mount /dev/hda0...' },
        { pct: 35, msg: 'init system modules...' },
        { pct: 48, msg: 'calibrating sensors...' },
        { pct: 62, msg: 'syncing GPS timebase...' },
        { pct: 75, msg: 'loading user profile...' },
        { pct: 88, msg: 'rendering HUD overlay...' },
        { pct: 95, msg: 'finalizing boot sequence...' },
        { pct: 100, msg: 'SYSTEM READY' },
      ],
    };

    this.barEl = document.querySelector(this.opts.barSelector);
    this.statusEl = document.querySelector(this.opts.statusSelector);
    this.currentStep = 0;
    this.timer = null;
    this.isRunning = false;
    this.audio = window.RevRank?.audioInstance;
  }

  start() {
    if (this.isRunning) return;
    this.isRunning = true;
    this.currentStep = 0;

    if (this.barEl) this.barEl.style.width = '0%';
    if (this.statusEl) this.statusEl.textContent = '> initializing...';

    const stepDuration = this.opts.duration / this.opts.steps.length;

    const runStep = () => {
      if (this.currentStep >= this.opts.steps.length) {
        this.isRunning = false;
        this.opts.onComplete && this.opts.onComplete();
        return;
      }

      const step = this.opts.steps[this.currentStep];

      // Update bar
      if (this.barEl) {
        this.barEl.style.width = step.pct + '%';
      }

      // Glitchy text reveal on status
      if (this.statusEl) {
        this.statusEl.textContent = '> ' + step.msg;
      }

      // Audio: text clicks
      if (this.opts.audio && this.audio) {
        for (let i = 0; i < step.msg.length; i += 3) {
          setTimeout(() => this.audio.textClick(), i * 8);
        }
        // Glitch SFX at certain points
        if (this.currentStep % 3 === 2) {
          setTimeout(() => this.audio.glitchSFX(0.2), 100);
        }
      }

      this.currentStep++;
      this.timer = setTimeout(runStep, stepDuration * (0.6 + Math.random() * 0.8));
    };

    runStep();
  }

  stop() {
    this.isRunning = false;
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = null;
    }
  }

  destroy() {
    this.stop();
  }
};


/* ===================================================================
   BOOT SEQUENCE — Full init helper (Matrix Rain + Audio + Glitch)
   Invoke once on page load to initialize all systems.
   =================================================================== */
RevRank.boot = function bootRevRank(config = {}) {
  const result = {};

  // 1. Matrix Rain
  if (config.matrixRain !== false) {
    const container = config.matrixRainContainer
      ? (typeof config.matrixRainContainer === 'string' ? document.querySelector(config.matrixRainContainer) : config.matrixRainContainer)
      : document.querySelector('.matrix-rain-container');
    if (container) {
      result.matrixRain = new RevRank.MatrixRain(container, config.matrixRainOpts || {});
      result.matrixRain.start();
    }
  }

  // 2. Audio
  const audio = new RevRank.Audio(config.audioOpts || {});
  window.RevRank.audioInstance = audio;

  // Init audio on first user interaction
  const initAudio = async () => {
    await audio.init();
    if (config.ambientHum !== false) {
      audio.startHum();
    }
    document.removeEventListener('click', initAudio);
    document.removeEventListener('touchstart', initAudio);
    document.removeEventListener('keydown', initAudio);
  };
  document.addEventListener('click', initAudio, { once: true });
  document.addEventListener('touchstart', initAudio, { once: true });
  document.addEventListener('keydown', initAudio, { once: true });

  result.audio = audio;

  // 3. Glitch Scheduler
  if (config.glitchTargets) {
    result.glitchScheduler = new RevRank.GlitchScheduler(
      config.glitchTargets,
      config.glitchOpts || {}
    );
    result.glitchScheduler.start();
  }

  return result;
};
