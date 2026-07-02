# App Icon Spec

## Android Adaptive Icon (API 26+)
- **Foreground:** `ic_launcher_foreground.xml` (vector)
  - Content: Speedometer needle forming an "R" or stylized "Rev" in MatrixGreen (#00FF41)
  - Stroke width: 2px, subtle drop-shadow glow
  - Safe zone: 66dp circle centered in 108x108dp canvas
- **Background:** `ic_launcher_background.xml`
  - Solid black (#000000), no texture, no gradients for adaptive compliance

## Legacy Icon (for Android 7.1 and below)
- 48x48, 72x72, 96x96, 144x144, 192x192 PNGs
- Same design as adaptive but rendered as flat PNG with black background

## Play Store Icon (High Res)
- 512x512 PNG
- Used for the Play Store listing header and Google Play Games
- Same "R" needle motif, larger, with subtle glow

## Wear OS (Optional)
- 384x384 round PNG with transparent background
- Simplified "R" needle, thicker strokes for small screens

## Color Check
- Foreground: #00FF41 (MatrixGreen) on pure black
- Active state (notification): #00FF41 with white pulse
- Monochrome: white outline only for themed icons
