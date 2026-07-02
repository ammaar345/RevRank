# Cloud Functions — functions/src/index.ts

Deploy to Firebase Functions. Written in TypeScript.

## Setup
```bash
cd functions
npm install firebase-admin firebase-functions
npm install -D typescript @types/node
```

---

## 1. Update Weekly Scores (Scheduled — every hour)
```typescript
import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
admin.initializeApp();

export const updateWeeklyScores = functions.pubsub
  .schedule("every 60 minutes")
  .onRun(async () => {
    const db = admin.firestore();
    const weekAgo = admin.firestore.Timestamp.fromDate(
      new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)
    );
    const trips = await db.collectionGroup("trips")
      .where("endTime", ">=", weekAgo)
      .where("score", ">", 0)
      .get();
    
    const userScores: Record<string, { total: number; count: number; weightedTotal: number; weightedCount: number }> = {};
    trips.forEach((doc) => {
      const trip = doc.data();
      const uid = trip.userId;
      if (!userScores[uid]) userScores[uid] = { total: 0, count: 0, weightedTotal: 0, weightedCount: 0 };
      userScores[uid].total += trip.score;
      userScores[uid].count += 1;
      // Weight by distance (longer trips count more)
      const weight = Math.max(1, trip.distanceKm / 10);
      userScores[uid].weightedTotal += trip.score * weight;
      userScores[uid].weightedCount += weight;
    });
    
    const batch = db.batch();
    Object.entries(userScores).forEach(([uid, scores]) => {
      const avgScore = Math.round(scores.weightedTotal / scores.weightedCount);
      batch.set(db.collection("weeklyScores").doc(uid), {
        uid,
        avgScore,
        tripCount: scores.count,
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      }, { merge: true });
    });
    await batch.commit();
  });
```

## 2. Reset Weekly Challenges (Scheduled — every Monday 00:00 UTC)
```typescript
export const resetWeeklyChallenges = functions.pubsub
  .schedule("0 0 * * 1")  // Monday midnight UTC
  .timeZone("UTC")
  .onRun(async () => {
    const db = admin.firestore();
    const users = await db.collection("users").get();
    const batch = db.batch();
    users.forEach((userDoc) => {
      const user = userDoc.data();
      const challenges = generateChallengesForUser(user);
      batch.set(db.collection("challenges").doc(user.uid), {
        userId: user.uid,
        challenges,
        weekStarting: admin.firestore.FieldValue.serverTimestamp(),
      });
    });
    await batch.commit();
  });

function generateChallengesForUser(user: any) {
  // Returns array of 3 challenge objects based on user's weakest categories
  // See ChallengeGenerator.kt for logic — mirror it here
  return [
    { type: "SCORE_THRESHOLD", minScore: 75, requiredTrips: 3, progress: 0, completed: false, xpReward: 100 },
    { type: "DISTANCE_GOAL", targetKm: (user.avgWeeklyDistanceKm || 20) * 1.2, progress: 0, completed: false, xpReward: 75 },
    { type: "BEAT_PERSONAL_BEST", currentPB: user.bestScore || 0, progress: 0, completed: false, xpReward: 125 },
  ];
}
```

## 3. Handle Referral (Firestore trigger)
```typescript
export const onReferralComplete = functions.firestore
  .document("users/{uid}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    const uid = context.params.uid;
    
    // Trigger: first trip completed by a referred user
    if (before.totalTrips === 0 && after.totalTrips === 1 && after.referredBy) {
      const referrerId = after.referredBy;
      const db = admin.firestore();
      
      // Add referral record
      await db.collection("users").doc(referrerId)
        .collection("referrals").doc(uid).set({
          referredUid: uid,
          referredAt: admin.firestore.FieldValue.serverTimestamp(),
          credited: true,
        });
      
      // Grant 30 days Pro via RevenueCat API for both users
      await grantRevenueCatEntitlement(referrerId, 30);
      await grantRevenueCatEntitlement(uid, 30);
      
      // Send FCM notification to referrer
      const referrer = await db.collection("users").doc(referrerId).get();
      const fcmToken = referrer.data()?.fcmToken;
      if (fcmToken) {
        await admin.messaging().send({
          token: fcmToken,
          notification: {
            title: "You earned 30 days Pro! 🎉",
            body: "Your friend completed their first trip. 30 days Pro added.",
          },
        });
      }
    }
  });

async function grantRevenueCatEntitlement(uid: string, days: number) {
  // POST to RevenueCat REST API: /v1/subscribers/{uid}/entitlements/pro/promotional
  const response = await fetch(`https://api.revenuecat.com/v1/subscribers/${uid}/entitlements/pro/promotional`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${functions.config().revenuecat.secret_key}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ duration: `P${days}D` }),  // ISO 8601 duration
  });
  return response.json();
}
```

## 4. Streak Push Notification (Scheduled — daily at 8 PM UTC)
```typescript
export const streakReminder = functions.pubsub
  .schedule("0 20 * * *")
  .timeZone("UTC")
  .onRun(async () => {
    const db = admin.firestore();
    const todayStart = new Date(); todayStart.setHours(0,0,0,0);
    
    // Find users with streaks who haven't driven today
    const users = await db.collection("users")
      .where("streakDays", ">", 0)
      .get();
    
    const messages: admin.messaging.Message[] = [];
    users.forEach((doc) => {
      const user = doc.data();
      const lastTrip = user.lastTripDate?.toDate();
      if (lastTrip && lastTrip < todayStart && user.fcmToken) {
        messages.push({
          token: user.fcmToken,
          notification: {
            title: `🔥 ${user.streakDays}-day streak at risk!`,
            body: "Take a drive today to keep your streak alive.",
          },
          data: { screen: "home" },
        });
      }
    });
    
    // Send in batches of 500
    for (let i = 0; i < messages.length; i += 500) {
      await admin.messaging().sendEach(messages.slice(i, i + 500));
    }
  });
```

## Deploy
```bash
firebase deploy --only functions
```

## Environment Config
```bash
firebase functions:config:set revenuecat.secret_key="YOUR_KEY"
```
