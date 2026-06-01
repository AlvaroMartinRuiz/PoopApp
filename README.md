# The Poop App

A fun-but-functional Android app for tracking digestive habits, with a social, gamified part. Log your bathroom visits, climb leaderboards with friends, and find public restrooms wherever you are.

▶️ **Watch the demo:** <https://www.youtube.com/watch?v=hUigo7fBZ8A>

[![Watch the demo](https://img.youtube.com/vi/hUigo7fBZ8A/hqdefault.jpg)](https://www.youtube.com/watch?v=hUigo7fBZ8A)

---

## What it does

- **Poop Tracker** — log bathroom visits, see trends over time, and get reminders & notifications.
- **Shitshare (social leagues)** — create or join private leagues with friends and battle for the leaderboard.
- **Bathroom Map** — Google Maps-powered finder for nearby public restrooms (handy when traveling).
- **Real-time sync** — logs, friends and leaderboards update live via Firebase Cloud Firestore.
- **Secure auth** — sign-up / sign-in through Firebase Authentication.

## Stack

| Layer | Tech |
|---|---|
| Mobile (native Android) | Kotlin · Android Studio · Material Design |
| Backend / data | Firebase Authentication · Cloud Firestore |
| Location | Google Maps SDK for Android |

## The team

Built by a four friends, everyone contributed in everything, but each member specialized in one specific part of the app:

| Contributor | Owned |
|---|---|
| **Álvaro Martín Ruiz** *(this repo)* | **Shitshare** — the social-network / gamified-leagues feature (leagues, friend invites, leaderboards) |
| Carlos Fernández-Yáñez | Firebase Authentication, user profiles, cloud storage |
| Alejandro Fonseca | Google Maps integration, bathroom-finder UI |
| Pablo Simón Martín | Core Poop Tracker, analytics and notifications |


## Run it locally

```bash
git clone https://github.com/AlvaroMartinRuiz/PoopApp.git
cd PoopApp
# open in Android Studio and let Gradle sync
```

⚠️ **Firebase note:** the app relies on Firebase Authentication + Firestore. To run with full functionality you'll need to register a Firebase project, enable Auth + Firestore, and drop a valid `google-services.json` into `app/`.
