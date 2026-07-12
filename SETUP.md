# RevRank — Setup & "What You Need To Do" Guide

_Last updated: 2026-07-11 (overnight session). Written for **sneaky**._

This is the single doc that tells you everything needed to take RevRank from
"compiles on my machine" to "runs on a real phone and can go to the Play Store."

---

## 0. TL;DR — the short version

| # | Thing | Needed to… | Effort | Blocks what |
|---|-------|-----------|--------|-------------|
| 1 | **Firebase `google-services.json`** | Sign-in, cloud database, storage | ~15 min, free | Almost everything online |
| 2 | **Google Maps API key** | Route Replay map | ~10 min, needs billing | Route Replay only |
| 3 | **RevenueCat + Play Console** | Selling Pro | Hours, $25 Play fee | Paywall / subscriptions only |
| 4 | **Run on emulator/phone** | Actually see it | ~5 min | Seeing your app |

**You do NOT need any of these for the app to *build*.** It already builds
(`app-debug.apk`). You need them for the app to *work correctly at runtime*.

Your details, already collected for you:
- **Package name (application ID):** `com.revrank` — use this EXACT string everywhere.
- **Debug SHA-1 fingerprint:** `5E:3B:FD:21:65:A3:C0:10:44:02:B3:70:C1:D5:9C:F4:00:16:52:5B`
  (this is your machine's debug key; you'll add a separate *release* SHA-1 later for the Play Store.)

---

## 1. GitHub repository

The repo already exists and is connected:

- **Remote:** https://github.com/ammaar345/RevRank.git (`origin`, branch `master`)
- As of this session, the overnight work (build fixes, re-skin, fonts, ~90 files)
  is committed and pushed (see the commit history). If you cloned this fresh,
  `git pull` gets you the latest.

Nothing for you to do here unless you want to move to a private repo or add collaborators.

---

## 2. Firebase (`google-services.json`) — DO THIS FIRST

### What it is
Firebase is Google's backend. RevRank uses **five** Firebase products:
- **Authentication** — Google Sign-In on the sign-in screen
- **Cloud Firestore** — the database (leaderboards, challenges, public profiles, trip sync)
- **Storage** — share-card image uploads
- **Analytics** — usage metrics
- **Crashlytics** — crash reports

`google-services.json` is a config file that points the app at *your* Firebase
project. **The one currently in `app/google-services.json` is a DUMMY I created**
so the build passes — every value in it is fake. The app will build with it but
any online feature will fail until you replace it.

### Steps
1. Go to **https://console.firebase.google.com** → **Add project** → name it
   "RevRank". The free **Spark plan** is fine to start.
2. In the project, click the **Android** icon ("Add app").
3. **Android package name:** type exactly `com.revrank`
   (⚠️ if this doesn't match, the build breaks — the google-services Gradle plugin checks it).
4. **Debug signing certificate SHA-1:** paste
   `5E:3B:FD:21:65:A3:C0:10:44:02:B3:70:C1:D5:9C:F4:00:16:52:5B`
   (required for Google Sign-In to work).
5. Click through, then **Download `google-services.json`**.
6. Replace the file at `app/google-services.json` with the one you downloaded.
   That's the only file change — the build reads it automatically.
7. Back in the Firebase console, **turn the products on**:
   - **Build → Authentication → Get started → Sign-in method → enable Google.**
   - **Build → Firestore Database → Create database** (start in *test mode* for now,
     lock it down later — see §6).
   - **Build → Storage → Get started.**

Once that real file is in place, auth + database + storage start working.

---

## 3. Google Maps API key

### What it's for
The **Route Replay** screen (a Pro feature) draws your trip on a map. Nothing else
uses Maps — the rest of the app runs fine without it.

### Current state
`app/src/main/AndroidManifest.xml` has a key at the `com.google.android.geo.API_KEY`
meta-data line. **Treat it as a placeholder — replace it with your own** (a leaked
key can be abused and billed to whoever owns it).

### Steps
1. Go to **https://console.cloud.google.com** (your Firebase project is already a
   Cloud project — pick it from the dropdown).
2. **APIs & Services → Library → search "Maps SDK for Android" → Enable.**
3. **APIs & Services → Credentials → Create credentials → API key.**
4. **Restrict it** (important): *Application restrictions* → Android apps → add
   package `com.revrank` + the debug SHA-1 above. *API restrictions* → limit to
   "Maps SDK for Android".
5. Paste the key into `AndroidManifest.xml`, replacing the `android:value` of the
   `com.google.android.geo.API_KEY` meta-data.
6. Maps needs **billing enabled** on the Cloud project (there's a big free monthly
   quota, but a card must be on file).

---

## 4. RevenueCat (Pro subscriptions)

### What it's for
The entire Pro paywall: buying/restoring subscriptions and the gate that unlocks
G-Force, Analytics, unlimited history, friends leaderboard, etc.

### Current state
`app/src/main/java/com/revrank/RevRankApplication.kt` has the literal string
`"YOUR_REVENUECAT_API_KEY"`. Until it's real, the paywall shows no products and
purchases do nothing.

### Steps (heaviest lift — leave for last)
1. Sign up at **https://www.revenuecat.com** (free until you're earning).
2. Create a project → add an app with package `com.revrank`.
3. Subscriptions are actually sold **through Google Play**, so you also need:
   - A **Google Play Console** developer account (**$25 one-time**).
   - The app uploaded to Play (at least to internal testing).
   - Subscription products created in the Play Console, then linked in RevenueCat.
4. Copy the **Public SDK Key** from RevenueCat → paste it in place of
   `"YOUR_REVENUECAT_API_KEY"` in `RevRankApplication.kt`.

**Skip this entirely if you just want to see the app run** — everything except
buying Pro works without it.

---

## 5. How to actually run it

### Option A — Android Studio (easiest for you)
1. Install **Android Studio** (free).
2. **File → Open** → select this project folder.
3. Let Gradle sync (it'll use the toolchain settings already in the project).
4. Create an emulator: **Device Manager → Create device** → pick e.g. Pixel 7,
   system image API 34.
5. Press **Run ▶**. The app installs and launches.

### Option B — command line (the toolchain is already installed on this machine)
The build tools live on `D:\android-build` (your C: drive is nearly full, so
everything was installed to D:). To build the APK:
```bash
export JAVA_HOME=/d/android-build/jdk/jdk-17.0.19+10
export GRADLE_USER_HOME=/d/android-build/gradle-home
export ANDROID_HOME=/d/android-build/sdk
cd /d/BlueprintAgents/problem-research/tripRankReplica
/d/android-build/gradle/gradle-8.7/bin/gradle.bat :app:assembleDebug --console=plain
```
Output APK: `app/build/outputs/apk/debug/app-debug.apk`

To install on a running emulator/connected phone:
```bash
/d/android-build/sdk/platform-tools/adb.exe install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 6. Security to-dos before you ship (don't skip)

- **Firestore rules:** the console starts you in *test mode* (anyone can
  read/write). Before real users, lock it down. There's a starter ruleset in
  `firestore-rules.md` in this repo.
- **Restrict every API key** to the package + SHA-1 (see §3 step 4).
- **Never commit the real `google-services.json` or keys to a public repo.** If
  the repo is public, add `app/google-services.json` to `.gitignore` and keep the
  real file local. (The current committed one is a harmless dummy.)

---

## 7. What's still stubbed in the CODE (keys alone won't finish these)

These are TODOs left in the source — they need code work, not just keys. Ranked by
how much they matter:

1. **Auth isn't wired to the flow.** The sign-in button navigates forward without
   actually authenticating, and several ViewModels use a hardcoded
   `userId = "current_user_id"`. Needs the Firebase Auth result plumbed through.
2. **Badge persistence.** Badges are calculated but never saved to the DB.
3. **Route Replay map.** The screen exists but the actual Google Map polyline
   drawing is a placeholder (also needs the Maps key from §3).
4. **Auto trip detection.** The activity-recognition receiver is stubbed; trips
   are started manually for now.
5. **Friends leaderboard data.** Wired to Firestore but there's no "add friend"
   flow yet.

---

## 8. Recommended order

1. **Firebase** (§2) — 15 min, unlocks the most.
2. **Run it** (§5) — see your app for the first time.
3. **Maps key** (§3) — when you want Route Replay.
4. **RevenueCat + Play** (§4) — when you're ready to monetize.
5. Work through the code stubs in §7 (I can do these — just point me at one).

---

_Questions or want me to knock out any of the §7 stubs? Just say which one._
