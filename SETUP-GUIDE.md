# RevRank — Keys & Monetization Setup

_For **sneaky**. Last updated 2026-07-13. This is the current, authoritative setup guide (supersedes the older `SETUP.md` where they disagree)._

Everything here is a **paste job into one file** per feature. None of it blocks the app from building or running — each key just switches one feature on.

## Your constants (use these exact values everywhere)

| Thing | Value |
|---|---|
| Package / application ID | `com.revrank` |
| Debug SHA-1 | `5E:3B:FD:21:65:A3:C0:10:44:02:B3:70:C1:D5:9C:F4:00:16:52:5B` |
| Firebase project | `revrank-cc0fc` (already set up) |
| Firebase Web client ID (Google Sign-In) | `37242091998-bjsniaabgsmndmiebt9utdbhgg72goji.apps.googleusercontent.com` |

## What's already done (don't redo)

- ✅ **Firebase** — real `google-services.json` is in `app/` (project `revrank-cc0fc`). Auth, Firestore, Storage, Analytics, Crashlytics wired. Google Sign-In provider enabled, SHA-1 registered. This file is **gitignored** — it stays on your machine, never pushed.
- ✅ **Google Sign-In code** — wired end to end (button → Google account → FirebaseAuth → real uid feeds the app). Only thing left is *you* testing it with a real Google account on a device (I can't type your password).

So the two things left are **Maps** and **RevenueCat**. Firebase is finished.

---

## 1. Google Maps API key (~10 min, needs billing)

**Unlocks:** the map in Route Replay (a Pro feature). Nothing else uses it.

1. Go to https://console.cloud.google.com and pick the **`revrank-cc0fc`** project (your Firebase project is already a Cloud project — choose it from the top dropdown).
2. **APIs & Services → Library** → search **"Maps SDK for Android"** → **Enable**.
3. **APIs & Services → Credentials → Create credentials → API key.** Copy the key it gives you.
4. **Restrict the key** (do this — an unrestricted key can be stolen and billed to you):
   - *Application restrictions* → **Android apps** → add package `com.revrank` + the debug SHA-1 above.
   - *API restrictions* → **Restrict key** → select only **Maps SDK for Android**.
5. **Enable billing** on the Cloud project (Billing → link a card). Maps has a large free monthly quota, but Google requires a card on file or map tiles won't load.
6. Paste the key into the app:
   - File: **`app/src/main/AndroidManifest.xml`**, around **line 32**.
   - Replace the `android:value` of the `com.google.android.geo.API_KEY` meta-data:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="PASTE_YOUR_KEY_HERE" />
     ```
   - ⚠️ There's a key sitting there now (`AIzaSy...U2SV4`) that is **not yours** — replace it.
7. Rebuild. Route Replay will draw the map.

> Note: the Route Replay screen currently shows a "MAP PLACEHOLDER" box even with a valid key — the actual Google Map view still needs to be coded in (Compose Maps). The key is step one; the map rendering is a follow-up code task.

---

## 2. RevenueCat + Play Console — monetization (hours, $25 one-time)

**Unlocks:** the entire Pro paywall — buying/restoring subscriptions and the gate that unlocks G-Force, Analytics, unlimited history, friends leaderboard, route replay. Leave this for last; everything except *buying Pro* works without it.

The code already guards this: until the real key is in, the paywall just stays off (no crash, no products shown).

### Step A — Google Play Console (this is the real work)
1. Create a **Google Play Console** developer account: https://play.google.com/console — **$25 one-time fee**.
2. Create the app (name "RevRank", package `com.revrank`).
3. Build a **signed release AAB** and upload it to at least the **Internal testing** track. (You'll also generate a *release* signing key here — add its SHA-1 to Firebase and the Maps key restrictions too, alongside the debug one.)
4. **Monetize → Subscriptions** → create your subscription product(s), e.g.:
   - `revrank_pro_monthly` (monthly)
   - `revrank_pro_annual` (annual)
   - Set prices, then **activate** them.

### Step B — RevenueCat
1. Sign up at https://www.revenuecat.com (free until you're earning).
2. Create a **project** → add an **app** with package `com.revrank`.
3. Connect it to Google Play (RevenueCat walks you through the Play service-account credential).
4. In RevenueCat, create an **entitlement** named exactly **`pro`** (the app checks `entitlements["pro"]`).
5. Create an **offering** and attach your monthly + annual Play products (as `$rc_monthly` / `$rc_annual` packages, or `annual`/`monthly` — the app reads `current.annual ?: current.monthly`).
6. Copy the **Public SDK Key** (Project settings → API keys → the Android/Public key, starts with `goog_...`).

### Step C — paste the key
- File: **`app/src/main/java/com/revrank/RevRankApplication.kt`**, **line 26**.
- Replace the placeholder:
  ```kotlin
  private val revenueCatApiKey = "goog_PASTE_YOUR_PUBLIC_SDK_KEY"
  ```
- Rebuild. The paywall now loads products, and purchases/restores work; the `pro` entitlement flips the whole app to Pro.

### Entitlement wiring (already in code — for reference)
- The app treats a user as Pro when RevenueCat reports `entitlements["pro"].isActive == true`.
- Paywall never appears before the user completes 3 trips (by design).

---

## 3. Security before you ship (don't skip)

- **Firestore rules:** the console likely started you in *test mode* (anyone can read/write). Lock it down before real users — there's a starter ruleset in `firestore-rules.md`.
- **Restrict every API key** to `com.revrank` + your SHA-1 (debug **and** release).
- **Never commit** the real `google-services.json` or any key to a public repo. `google-services.json` is already gitignored; the Maps key lives in a tracked file (`AndroidManifest.xml`), so if the repo is/goes public, move it to a gitignored `local.properties` / `secrets.properties` setup.

---

## 4. Build & install (reference)

```bash
export JAVA_HOME=/d/android-build/jdk/jdk-17.0.19+10
export GRADLE_USER_HOME=/d/android-build/gradle-home
export ANDROID_HOME=/d/android-build/sdk
cd /d/BlueprintAgents/problem-research/tripRankReplica
/d/android-build/gradle/gradle-8.7/bin/gradle.bat :app:assembleDebug --console=plain
# install on a connected phone or running emulator:
/d/android-build/sdk/platform-tools/adb.exe install -r app/build/outputs/apk/debug/app-debug.apk
```

## 5. Priority order

1. **Test real Google Sign-In + a real ~1km drive on a physical phone** — this is the one thing that proves the whole product works and that only you can do. (Firebase is already set up, so this should just work.)
2. **Maps key** — quick, unlocks the Route Replay map (after the map view is coded).
3. **RevenueCat + Play Console** — the big one; only needed when you're ready to actually sell Pro.
