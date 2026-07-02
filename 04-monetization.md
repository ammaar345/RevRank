# 04 — Monetization Strategy

## Core Principle
**Never make the free user feel punished. Make the Pro user feel powerful.**

The distinction is critical. A user who feels punished leaves and writes a 1-star review. A user who sees Pro features and thinks "I want that" converts.

---

## Tier Structure

### Free (Forever)
Everything a casual driver needs to enjoy the app:
- Unlimited trip tracking & auto-detection
- Trip score (0–100) + grade letter
- Rank system + XP (all 7 tiers earnable)
- All 30 badges (earnable, never paywalled)
- Last 10 trips history
- Weekly recap shareable card
- Basic stats (speed, distance, time)
- Weekly challenges (3 per week)
- Global leaderboard (view only, top 100)
- Friend challenges (send and receive)
- Streak system (1 freeze/week)

### Pro ($3.99/mo or $24.99/yr — 48% saving)
For the driver who wants depth:
- Everything in Free
- Unlimited trip history
- Route replay with color-coded map
- G-force radial visualizer
- Advanced analytics (trends, heatmaps, radar)
- Friends leaderboard (compete with specific friends)
- Custom route names & tags
- CSV data export
- Home screen widget
- Unlimited streak freezes
- Early access to new features

### Enterprise (Future — $19.99/seat/mo)
For fleets and driving instructors:
- All Pro features
- Multi-driver dashboard
- Comparative scoring across drivers
- Weekly/monthly driver reports
- Admin console (web)
- API access

---

## Pricing Psychology

**Why $3.99/mo:**
- Below the "is this worth it?" reflex threshold (~$4.99)
- Annual at $24.99 = ~$2.08/mo — feels like a steal
- Comparable to: Waze Carpool, Spotify's add-ons, app subscriptions in this category
- RevenueCat data shows $2.99–$4.99 is the sweet spot for utility apps

**Annual push:**
- Default toggle to "Annual" on paywall screen
- Show per-month savings prominently: "Just $2.08/month"
- Annual users churn 3–4x less than monthly

---

## Paywall Trigger Points (Carefully Chosen)

Do NOT trigger paywall before:
- User has completed their 1st trip (absolute rule)
- User has seen their first score
- User has earned their first badge

**Trigger paywall at these moments ONLY:**
1. User taps "Trip History" and has more than 10 trips (inline upsell)
2. User taps "Route Replay" on a trip card (blurred preview + "Unlock with Pro")
3. User taps "Advanced Stats" in analytics (blurred chart + upsell)
4. User taps "Friends Leaderboard" (teaser showing friends with blurred scores)
5. User hits their streak freeze limit (soft prompt: "Never lose your streak")
6. Weekly recap (small "Go Pro" chip, non-intrusive)

**NEVER trigger at:**
- App first launch
- Every session start
- During an active trip
- Mid-onboarding

---

## Free Trial Strategy

- 7-day free Pro trial on FIRST paywall encounter only
- No credit card required on Android (Play Store handles it)
- Trial starts from first paywall tap, not from install
- Trial ending reminder: push notification 2 days before, 1 day before
- If user converts during trial: no interruption, seamless continuation

---

## Referral Program ("RideShare Pro")

Mechanics:
- Every Pro user gets a unique referral link
- Share link → friend installs → friend gets 1 month free Pro trial (extended from 7 days)
- Referrer gets 1 month free added to subscription
- Max 12 months free per year from referrals
- Tracked via Firebase Dynamic Links + Firestore

Why this works:
- Cost per acquisition = 1 month of Pro (~$1.67 at annual rate)
- Typical CAC for apps via paid = $3–8
- Referrals have 3x higher LTV than paid installs

---

## Revenue Projections

**Conservative scenario (Month 6):**
- 5,000 MAU
- 4% conversion = 200 Pro users
- Mix 60% annual, 40% monthly
  - 120 × $24.99 = $2,999/yr = $250/mo
  - 80 × $3.99 = $319/mo
- **Total: ~$570/mo**

**Growth scenario (Month 12):**
- 25,000 MAU
- 5% conversion = 1,250 Pro users
- **Total: ~$3,500–4,200/mo**

**At $4k MRR:** Covers server costs + developer salary. App becomes self-sustaining.

---

## Future Revenue Streams

### Ads (Month 6+, Free Tier Only)
- One banner on Trip History screen (never on HUD, never on trip end)
- AdMob integration
- Disappears with Pro
- Expected: $0.50–1.50 CPM = ~$50–150/mo at 5k MAU (supplementary only)

### Branded Challenges (Month 9+)
- Car brands sponsor weekly challenges
- "BMW Challenge: Score 90+ on 3 motorway trips this week"
- Winner gets brand discount/voucher
- Revenue: sponsorship deals ($500–2,000 per campaign)

### Data Insights (Year 2, Anonymised & Aggregated)
- Sell anonymised driving pattern data to:
  - Traffic analytics companies
  - Urban planning consultants
  - Insurance actuaries (aggregate only, GDPR compliant)
- Requires privacy policy update + explicit opt-in from users
- Potential: $0.10–0.50 per active user/month at scale

### Insurance Partnership (Year 2)
- Partner with usage-based insurance (UBI) providers
- User shares their TripRank score with insurer
- User gets insurance discount based on score
- TripRank gets referral fee
- Companies to approach: Markel, By Miles, Zego, Root Insurance

---

## Churn Reduction

Key churn moments and fixes:

| Churn Trigger | Fix |
|--------------|-----|
| "I don't drive every day" | Streaks have weekly flex — weekly active counts |
| "It's too expensive" | Annual pricing, free trial, referral credits |
| "The free tier is enough" | Route replay is compelling — it's visual, not just data |
| "I forgot about it" | Weekly recap push notification (re-engagement) |
| "GPS was inaccurate" | Kalman filter fix (Branch 08) |
| "My streak broke" | Streak freeze feature |
