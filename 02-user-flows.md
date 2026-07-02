# 02 — User Flows

## Flow 1: New User First Session

```
INSTALL
  └─► SPLASH (matrix rain logo reveal, 1.5s)
        └─► ONBOARDING SCREEN 1 (speedometer animation)
              └─► ONBOARDING SCREEN 2 (sample trip)
                    └─► ONBOARDING SCREEN 3 (free vs pro)
                          ├─► [Try Pro] → FREE TRIAL ACTIVATION → SIGN IN
                          └─► [Start Driving] → SIGN IN OPTIONS
                                ├─► Google Sign-In
                                └─► Apple Sign-In
                                      └─► USERNAME PICKER
                                            └─► VEHICLE TYPE SELECTOR
                                                  └─► PERMISSIONS FLOW
                                                        ├─► [Grant Location] → next
                                                        └─► [Deny] → explanation modal → retry
                                                              └─► HOME SCREEN (first time, empty state)
                                                                    └─► GUIDED TOOLTIP: "Take a drive to get your first score"
```

---

## Flow 2: Trip Detection & Completion (Auto-Mode)

```
USER IN CAR, DRIVING
  └─► [Background Service] detects speed >5km/h for 30s
        └─► FOREGROUND NOTIFICATION appears: "Trip started ● Recording"
              └─► [User taps notification] → LIVE HUD SCREEN
              └─► [User ignores] → background tracking continues
                    └─► [Stationary 3+ minutes] → AUTO-END TRIP
                          └─► TRIP END PROCESSING (score calculation, ~2s)
                                └─► PUSH NOTIFICATION: "Trip complete. Score: 87"
                                      └─► [User taps notification] → TRIP END SCORE REVEAL
                                      └─► [User ignores] → trip saved to history
```

---

## Flow 3: Trip End → Share → Viral Install

```
TRIP END SCORE REVEAL
  └─► Score animation completes
        ├─► [Badge earned] → BADGE UNLOCK ANIMATION → return to score reveal
        ├─► [Rank-up] → RANK UP FULL SCREEN → return to score reveal
        └─► User sees [SHARE] button
              └─► SHARE CARD RENDERS (Canvas, ~1s)
                    └─► Native Android share sheet opens
                          ├─► [Instagram Stories] → card posted with TripRank sticker
                          ├─► [WhatsApp] → card + deep link
                          └─► [Other] → card + deep link
                                └─► RECIPIENT opens link
                                      ├─► [Has app] → opens TripRank, sees shared trip
                                      └─► [No app] → Play Store, installs, sees trip context
```

---

## Flow 4: Returning User Daily Loop

```
OPENS APP
  └─► HOME SCREEN
        ├─► Sees streak count → motivation to drive
        ├─► Sees weekly challenge progress → specific goal
        └─► Sees last trip card → taps for detail
              └─► TRIP DETAIL SCREEN
                    ├─► [Route Replay] → if Pro: ROUTE REPLAY
                    │                  → if Free: SOFT PAYWALL
                    └─► [Share] → SHARE FLOW
```

---

## Flow 5: Free → Pro Conversion

```
TRIGGER MOMENT: User has >10 trips, taps "older history"
  └─► INLINE UPSELL in Trip History
        └─► [Learn More] → PRO PAYWALL SCREEN
              └─► Monthly / Annual toggle (Annual default)
                    └─► [Start 7-Day Free Trial]
                          └─► Play Store subscription flow
                                ├─► [Success] → PRO ACTIVATED SCREEN (matrix rain celebration)
                                │               → returns to whatever they were doing
                                └─► [Cancel] → returns to app, no hard feelings
```

---

## Flow 6: Friend Challenge

```
USER on TRIP DETAIL
  └─► [Challenge a Friend]
        └─► CHALLENGE LINK GENERATED (deep link with route hash + score)
              └─► Native share sheet
                    └─► FRIEND opens link
                          └─► PLAY STORE if needed
                                └─► OPENS APP to "You've been challenged" screen
                                      └─► Shows challenger name, their score, route
                                            └─► [Accept] → trip tracking, same route detected
                                                  └─► TRIP END: comparison shown
                                                        └─► PUSH to challenger: "Friend beat your score!"
```

---

## Flow 7: Username Recovery (Bug Fix)

```
REINSTALL / NEW DEVICE
  └─► SIGN IN with Google (same account)
        └─► Firebase Auth → same UID returned
              └─► Firestore fetches user by UID
                    └─► Username, rank, XP, badges all restored
                          └─► HOME SCREEN — exactly as left
```
No username loss. Account is the identity, not the device.

---

## Error States & Edge Cases

| Scenario | Handling |
|---------|---------|
| GPS signal lost during trip | Show signal indicator, continue with last known, interpolate |
| App killed during trip | WorkManager restarts foreground service, trip continues |
| Trip too short (<500m) | Don't save, show "Drive further to record a trip" toast |
| Score calculation fails | Default to 0, flag for retry, never crash |
| Firestore offline | Room local DB is source of truth, sync queue when online |
| Auth token expired | Silent refresh, if fails → sign in screen with "Session expired" |
| Play Store billing error | RevenueCat handles retry, graceful error message |
| Username taken | Real-time check, suggest 3 alternatives |
