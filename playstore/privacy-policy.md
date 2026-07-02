# Privacy Policy — RevRank

**Effective Date:** 25 June 2026
**Last Updated:** 25 June 2026
**App Operator:** BlueprintAgents Development
**Contact:** privacy@reverank.app

---

## 1. Introduction

RevRank ("we", "us", "our") is committed to protecting your privacy. This Privacy Policy explains what data we collect, how we use it, and your rights regarding your information when you use the RevRank mobile application.

We designed RevRank with privacy as a core principle. Your location and trip data is stored locally on your device by default. Cloud sync is entirely optional and only activated with your explicit consent.

---

## 2. Information We Collect

### 2.1 Information You Provide Directly

- **Email Address**: Account creation via Google Sign-In (Required)
- **Username**: Public display on leaderboards and profiles (Required)
- **Profile Photo**: Public profile display (Optional)
- **Feedback / Reports**: App improvement and support (Voluntary)

### 2.2 Information Collected Automatically

| Data | Purpose | Local | Cloud |
|------|---------|-------|-------|
| GPS Coordinates | Calculate ride scores, route | Yes (default) | Only if synced |
| Accelerometer Data | Calculate braking / cornering scores | Yes | Never |
| Trip Metadata | Gamification, history, leaderboards | Yes | Only if synced |
| Device Info | Crash reporting, performance | Limited | Yes (Crashlytics) |
| Usage Analytics | Feature usage, funnels | No | Yes (anonymized) |

### 2.3 Location Data — Details

RevRank collects precise GPS coordinates **only during active trip tracking**:
- **When**: Only when you press "Start Ride" until you press "End Ride"
- **How**: Foreground service with persistent notification
- **Where stored**: On your device by default; optionally in your Firebase account if cloud sync is enabled
- **Who can see**: Only you. We never share raw GPS data with other users or third parties

---

## 3. How We Use Your Data

- Calculate driving and riding scores, speed, distance, and route data
- Generate trip history, analytics, and performance trends
- Power leaderboards and social features (username and score data only — never raw GPS)
- Provide crash reporting and app performance debugging
- Improve app functionality and user experience through aggregated analytics

---

## 4. Data Sharing

We do not sell, trade, or rent your personal information.

We may share anonymized, aggregated data for analytics and benchmarking purposes. This data cannot be used to identify individual users.

---

## 5. Data Security

- All data transmission uses industry-standard encryption (TLS 1.2+)
- Cloud data is stored in Firebase with Firebase Security Rules restricting access to account owners only
- Local data is secured by your device's operating system encryption
- We follow the principle of data minimization — we collect only what is necessary

---

## 6. Your Rights

- **Access**: View all data associated with your account in-app
- **Correction**: Update your profile information at any time
- **Export**: Download your trip data in CSV format (Pro feature)
- **Deletion**: Request complete deletion of your account and all associated data
- **Withdrawal**: Disable cloud sync at any time; your data stays local

---

## 7. Third-Party Services

| Service | Purpose | Data Shared |
|---------|---------|-------------|
| **Firebase** | Authentication, analytics, cloud storage | Email (hashed), usage events, crash reports |
| **RevenueCat** | Subscription management | Purchase receipts, subscription status |
| **Google Play Services** | Authentication, location services | OAuth token (transient), GPS data |

---

## 8. Children's Privacy

RevRank is not intended for users under 13 years of age. We do not knowingly collect personal information from children. If you believe a child has provided us with personal information, please contact us and we will delete it.

---

## 9. Changes to This Policy

We may update this Privacy Policy from time to time. Changes will be posted with an updated effective date. For significant changes, we will notify you within the app.

---

## 10. Contact Us

For privacy-related questions, data access requests, or deletion requests:

**Email:** privacy@reverank.app
**In-App:** Profile > Settings > Privacy & Data

---

*RevRank Privacy Policy v1.0 — 25 June 2026*
