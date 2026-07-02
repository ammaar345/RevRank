# Branch 07 — Monetization UX

## Prerequisites
Branch 06 merged. All Pro features exist with ProGate wrappers.

## RevenueCat Setup

```kotlin
// Application.onCreate():
Purchases.configure(
    PurchasesConfiguration.Builder(this, "YOUR_REVENUECAT_API_KEY").build()
)

// Entitlement ID in RevenueCat dashboard: "pro"
// Products to create in Play Console:
//   triprank_pro_monthly  — $3.99/month
//   triprank_pro_annual   — $24.99/year
// Create offering in RevenueCat: "default" with both products
```

### PurchaseManager.kt
```kotlin
@Singleton
class PurchaseManager @Inject constructor() {
    val isProFlow: Flow<Boolean> = callbackFlow {
        Purchases.sharedInstance.addCustomerInfoUpdateListener { info ->
            trySend(info.entitlements["pro"]?.isActive == true)
        }
        awaitClose()
    }
    
    suspend fun purchasePackage(activity: Activity, packageToPurchase: Package): PurchaseResult
    suspend fun restorePurchases(): Boolean
    suspend fun getOfferings(): Offerings?
}
```

---

## PaywallScreen.kt

Triggered by: explicit "Get Pro" tap, or `ProUpsellBanner` CTA.
NOT triggered automatically on session start. NOT a full-screen modal on first launch.

Layout:
```
Dark full-screen. Edge-to-edge.

TOP: [✕] dismiss button (top-right, small)

CENTER HERO:
  "UNLOCK YOUR FULL POTENTIAL" (Share Tech Mono, 22sp, MatrixGreen)
  [Animated: matrix rain briefly, resolves into "PRO"]

FEATURE LIST (animated in sequentially, 100ms stagger):
  ◈  Unlimited trip history
  ◈  Route replay with score overlay  
  ◈  G-force visualizer
  ◈  Advanced analytics & trends
  ◈  Friends leaderboard
  ◈  Home screen widget
  ◈  Unlimited streak freezes
  ◈  CSV data export
  (◈ icon = custom diamond icon, MatrixGreen)

PRICING TOGGLE (Monthly / Annual):
  Segmented control, default = ANNUAL
  Monthly: "$3.99 / month"
  Annual:  "$24.99 / year  ·  SAVE 48%"  ← amber chip "SAVE 48%"

FREE TRIAL BANNER (first paywall encounter only):
  "✓ 7-day free trial included — cancel anytime"
  Shown as green pill above CTA button

CTA BUTTON:
  "START FREE TRIAL" (if first encounter)
  or "UPGRADE TO PRO" (if trial expired)
  Full-width, 56dp, MatrixGreen background, Void text

RESTORE LINK:
  "Restore purchases" — text, small, dim, bottom
```

### First encounter detection:
Store `hasSeenPaywall: Boolean` in DataStore. Show trial banner only when false. Set to true after first paywall show.

---

## ProUpsellBanner.kt (Inline Soft Gate)

Used inside ProGate fallback. Shows instead of the Pro feature content.

```
┌──────────────────────────────┐
│  [blurred/dimmed feature]    │  ← actual feature content, 80% alpha + blur
│  ┌────────────────────────┐  │
│  │ 🔒  PRO FEATURE        │  │
│  │ {featureDescription}   │  │
│  │ [UNLOCK PRO  →]        │  │  ← taps → PaywallScreen
│  └────────────────────────┘  │
└──────────────────────────────┘
```

Takes a `featureDescription: String` parameter so each gate has context-specific copy:
- Trip history: "See all your trips, not just the last 10"
- Route replay: "Watch your drive with a live score overlay"
- Analytics: "Track your improvement over weeks and months"
- Friends board: "See how you rank against friends"

---

## Pro Activated Screen

After successful purchase:
```
Full screen. Black.
Matrix rain animation (2 seconds)
Resolves to: "PRO ACTIVATED" (Share Tech Mono, 28sp, MatrixGreen)
             [animated rank badge upgrade indicator]
"All features unlocked. Drive well." (14sp, TextSecondary)
Auto-dismiss after 3s → returns to previous screen
```

---

## Trial End Handling

RevenueCat sends webhook to Cloud Function when trial expires.
Cloud Function sets `isPro = false` in Firestore.
On next app foreground: Purchases SDK emits new CustomerInfo → `isProFlow` emits false.
App gracefully disables Pro features (ProGate shows fallback).
Push notification (FCM) day before trial ends: "Your Pro trial ends tomorrow. Keep your rank stats and history — upgrade now."
Push notification on trial end: "Your Pro trial has ended. Your data is safe — upgrade anytime to continue."

---

## Referral Pro Credit (Cloud Function)

```typescript
// functions/src/handleReferral.ts
export const onReferralInstallComplete = functions.firestore
  .document('users/{uid}')
  .onUpdate(async (change, context) => {
    const newData = change.after.data();
    const oldData = change.before.data();
    // When user completes first trip (totalTrips goes from 0 to 1):
    if (oldData.totalTrips === 0 && newData.totalTrips === 1 && newData.referredBy) {
      const referrerId = newData.referredBy;
      // Add 30 days Pro to referrer via RevenueCat API (grant entitlement)
      await grantProCredit(referrerId, 30);
      // Add 30-day trial to new user
      await grantProCredit(context.params.uid, 30);
    }
  });
```

---

## Acceptance Criteria
- [ ] RevenueCat initialises on app start
- [ ] Monthly/annual products load from RevenueCat offerings
- [ ] Purchase flow completes and `isProFlow` emits true immediately
- [ ] Restore purchases works correctly
- [ ] Trial banner shown only on first paywall encounter
- [ ] ProUpsellBanner shows context-specific copy per feature
- [ ] Pro activated screen plays after successful purchase
- [ ] Trial end push notification fires correctly
- [ ] Referral credits applied via Cloud Function
- [ ] Paywall dismissible at all times (no forced hard block)
- [ ] User hits NO paywall before completing 3 trips (enforced in PaywallTriggerManager.kt)

## PaywallTriggerManager.kt
```kotlin
object PaywallTriggerManager {
    fun shouldShowPaywall(user: User, trigger: PaywallTrigger): Boolean {
        if (user.totalTrips < 3) return false  // NEVER before 3 trips
        if (user.isPro) return false
        return true
    }
}
```
