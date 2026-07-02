# MASTER HANDOVER — Read This First

You are building the TripRank Android app redesign. This is a complete product overhaul with the following package of specification files. Read ALL of them before writing any code.

## Read in this order:
1. `docs/01-product-strategy.md` — what we're building and why
2. `docs/02-tech-stack.md` — every technology decision
3. `design-system/01-visual-identity.md` — the Matrix/retro visual language (CRITICAL — apply throughout)
4. `design-system/02-component-library.md` — every reusable component
5. `wireframes/01-screens.md` — ASCII wireframe of every screen
6. `wireframes/02-user-flows.md` — all user journeys
7. `docs/03-feature-roadmap.md` — all features by branch
8. `docs/04-monetization.md` — free/Pro tiers, paywall rules

## Then execute branches in order:
Each file in `claude-code-prompts/` is a self-contained implementation brief for one git branch.

```
branch-00-foundation.md   ← START HERE — project setup, auth, nav, design tokens
branch-01-onboarding.md
branch-02-trip-tracking.md
branch-03-trip-summary.md
branch-04-gamification.md
branch-05-social.md
branch-06-pro-features.md
branch-07-monetization.md
branch-08-performance.md
branch-09-polish.md
```

## Non-negotiable design rules:
- **Dark mode only.** No light theme. Background is always #000000 or #0A0A0A.
- **Share Tech Mono** for all numbers, scores, speeds, rank names.
- **Rajdhani** for all body text, labels, buttons.
- **MatrixGreen (#00FF41)** is used sparingly — hero moments only (score reveal, rank-up, CTAs).
- Score colors are semantic: green (90+), lime (75+), amber (55+), orange (35+), red (below 35).
- Cards have **0dp** border radius. Buttons have **4dp**. Modals have **8dp**.
- Every number that changes should **animate** (count up, fill in). No static data appearances.
- Paywall **never** appears before user completes 3 trips. Enforced in `PaywallTriggerManager`.

## Critical bugs to fix first (before any new features):
1. **Username loss on reinstall** — solved by Firebase Auth UID + Firestore lookup (Branch 00)
2. **GPS lag** — solved by Kalman filter + network location seed (Branch 02 + 08)  
3. **Background trip kill on Samsung** — solved by foreground service + Doze handling (Branch 02 + 08)

## Project details:
- Language: Kotlin
- UI: Jetpack Compose (Material 3)
- Min SDK: 26 (Android 8.0)
- Target SDK: 34
- Architecture: MVVM + Clean Architecture + Hilt DI
- Local DB: Room
- Remote: Firebase (Auth, Firestore, Storage, Functions, FCM)
- Subscriptions: RevenueCat
- Analytics: Firebase Analytics + Crashlytics

## Firebase project:
Add `google-services.json` to `app/` directory (from Firebase Console).
Create project at console.firebase.google.com with:
- Authentication: enable Google Sign-In + Apple Sign-In
- Firestore: create in production mode, apply rules from `features/firestore-rules.md`
- Storage: default bucket
- Functions: deploy from `features/cloud-functions.md`
- Cloud Messaging: enabled by default

## RevenueCat:
- Register at app.revenuecat.com
- Create app → Android → add Play Store credentials
- Create entitlement: `pro`
- Create products: `triprank_pro_monthly` ($3.99) and `triprank_pro_annual` ($24.99)
- Create offering: `default` with both products as packages
- Add API key to `BuildConfig` or `local.properties`

---

Start with `branch-00-foundation.md`. Create the branch, implement everything in it, confirm it builds and all acceptance criteria pass, then move to `branch-01-onboarding.md`.
