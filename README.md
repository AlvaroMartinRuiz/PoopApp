# The Poop App

A fun but useful mobile application developed for Android devices using Kotlin. **The Poop App** allows users to log their bathroom visits, track digestive habits over time, and interact with friends through a social and gamified experience.

## Features

- **Poop Tracker**: Log your bathroom visits and track your digestive health habits over time. Includes an alert and notifications system.
- **Social Leagues & Leaderboards (Shitshare)**: Create or join leagues with friends to see who can claim the top spot on the leaderboard.
- **Public Bathroom Map**: Integrated with Google Maps to help users locate and review nearby public bathrooms, making it especially handy while traveling.
- **Real-Time Data**: Fast synchronization for logs, user activity, and leaderboard changes powered by Firebase Cloud Firestore.
- **Secure Authentication**: User sign-up, login, and robust cloud storage handled via Firebase Authentication.

## Technologies Used

- **Frontend / Android Native**: Kotlin, Android Studio
- **Backend / Cloud**: Firebase (Authentication, Cloud Firestore)
- **Maps API**: Google Maps SDK

## The Team (Group 196)

- **Alejandro Fonseca**: Google Maps integration and location-based features.
- **Carlos Fernández-Yáñez**: Firebase integration, user authentication, and cloud storage.
- **Pablo Simón Martín**: Core Poop Tracker, analytics, and Notifications system.
- **Álvaro Martín Ruiz**: *Shitshare*, the app's gamified social networking feature.

*Developed as part of coursework under the supervision of Boni García Gutiérrez.*

## 🚀 How to Run Locally

If you'd like to test or edit The Poop App locally, you can open it in Android Studio:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/carlosfya/PoopApp.git
   ```
2. **Open the project in Android Studio:**
   Let Gradle complete the build and dependency resolution.
3. **⚠️ Important Note on Firebase Permissions:** 
   Because this application relies heavily on a secure Firebase backend (Authentication and Firestore), you will need a valid `google-services.json` file to run it with full capabilities. Unless you have authorization for the original Firebase project, you will need to create your own Firebase project, register the app, and generate a new `google-services.json` file to place in the `app/` directory.
4. **Run on an Emulator or Device** via Android Studio.

## VIDEO DEMO:
If you like to have a look at how the app works, see this video where we show the functionalities of the app!
https://www.youtube.com/watch?v=hUigo7fBZ8A
