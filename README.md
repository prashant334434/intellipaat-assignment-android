# 📚 Learning Dashboard

A production-style **offline-first Learning Dashboard Android app** built with **Kotlin, Jetpack Compose, Material 3, MVVM, Clean Architecture, and Room**.

The project focuses on a practical learning experience with local-first data handling, reactive UI updates, offline support, and a clean separation between UI, business logic, and data.

## 📱 Demo

### APK

You can download and install the latest debug APK here:

**[⬇️ Download Learning Dashboard APK](https://github.com/prashant334434/intellipaat-assignment-android/blob/main/app/release/app-release.apk)**

> Android may ask you to allow installation from unknown sources when installing the APK manually.

### 🎥 Demo Video

<!-- Replace VIDEO_URL with your uploaded video URL -->

<video src="VIDEO_URL" controls width="100%"></video>

If the embedded video doesn't render on GitHub, you can also add a normal link:

**[▶️ Watch the Learning Dashboard Demo](https://drive.google.com/file/d/1eWNNgxt_He8jEQN6ErMAB-b_Gd8rQ_Fg/view?usp=sharing)**

---

## ✨ What the App Includes

### 🔐 Login & Demo Shortcuts

The login screen includes quick demo buttons so the application can be tested without manually entering credentials.

- **Valid credentials** — instantly fills valid login details
- **Invalid credentials** — simulates a failed login
- Email and password validation
- Clear authentication error states

### 🌐 Online / Offline Simulator

The dashboard includes an online/offline switch directly in the TopAppBar.

This makes it easy to test offline behavior without enabling airplane mode or changing the source code.

```text
🌐 Online
📴 Offline
```

### 📊 Learning Momentum

The dashboard provides a quick overview of learning progress:

- Overall completion percentage
- Number of enrolled courses
- Completed lessons
- Course progress
- Lesson completion status

The progress updates immediately when a lesson is completed.

### 🔍 Search & Filtering

Courses can be searched by:

- Course name
- Instructor

The dashboard also includes filters for:

- All Courses
- In Progress
- Completed

### 💀 Skeleton Loading

Instead of showing a generic progress spinner, the app uses animated skeleton/shimmer cards while data is loading.

This keeps the UI feeling responsive during data refreshes.

### ✅ Interactive Lessons

Lessons can be marked as completed directly from the course screen.

The UI reacts immediately:

```text
○ Pending  →  ✓ Completed
```

Course progress is recalculated automatically.

When all lessons are completed, the app displays a completion state/celebration banner.

---

# 🏗️ Architecture

The application follows **MVVM + Clean Architecture** with a unidirectional data flow.

```text
┌───────────────────────────────┐
│        Jetpack Compose        │
│             UI                │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│          ViewModel            │
│       StateFlow / UDF         │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│         Domain / Core         │
│                               │
│ Entities • Use Cases          │
│ Business Rules • Repositories │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│          Repository           │
│                               │
│     Remote + Local Data       │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│          Room Database        │
│          SQLite / Flow        │
└───────────────────────────────┘
```

### Why this approach?

I wanted the business logic to stay independent from Android UI and framework code.

The `:core` module contains the domain models, validation, progress calculations, and repository contracts. Because it is pure Kotlin/JVM, these parts can be tested without an emulator.

The Android app handles Compose UI, ViewModels, Room, and platform-specific functionality.

This also leaves a clean path toward Kotlin Multiplatform if the domain layer needs to be shared with iOS in the future.

---

# 📴 Offline-First Approach

Room acts as the local **Single Source of Truth**.

The basic flow is:

```text
Remote API
    │
    ▼
Repository
    │
    ├── Merge remote course data
    │
    ├── Preserve local lesson progress
    │
    ▼
Room Database
    │
    ▼
Flow
    │
    ▼
ViewModel
    │
    ▼
Compose UI
```

### Local-first writes

When a user completes a lesson, the change is written to Room immediately.

The UI does not need to wait for a network request.

This means lesson completion continues to work even when the device is offline.

### Preserving progress during sync

When fresh course data arrives from the API, locally completed lessons are preserved.

The repository uses a merge strategy rather than blindly replacing the local database.

```text
Remote Course Data
        +
Local Completion State
        ↓
Merged Local State
        ↓
Room
        ↓
UI
```

### Offline fallback

If the network request fails:

1. Existing Room data remains available.
2. The UI continues displaying cached courses.
3. The app shows the current offline state.
4. Local lesson updates continue to work.

---

# 🔐 Authentication & Security

The current project uses demo authentication for evaluation purposes.

For a production application, I would use:

### Access Token

Keep short-lived access tokens in memory wherever practical.

### Refresh Token

Store refresh tokens using Android Keystore-backed encrypted storage.

Possible implementation:

- Android Keystore
- AES-256-GCM
- Google Tink
- Encrypted DataStore strategy

Plaintext `SharedPreferences` should not be used for sensitive tokens.

### Network Security

A production implementation should also include:

- HTTPS only
- TLS
- Certificate pinning where appropriate
- Automatic 401 handling
- Secure token refresh
- Proper logout/session invalidation

---

# 📈 Scaling the Application

If this application grows to **1 million users and hundreds of courses**, I would make the following changes.

### 1. Paging

Use **Paging 3 + RemoteMediator + Room** instead of loading the entire course catalog at once.

Course metadata can be paginated while detailed lessons are loaded only when needed.

### 2. Offline Sync Queue

Introduce a local outbox for actions such as:

```text
Lesson Completed
       ↓
Local Outbox
       ↓
WorkManager
       ↓
API
       ↓
Server
```

Each event should have an idempotent ID so retries don't create duplicate updates.

### 3. HTTP Caching

Use:

- `ETag`
- `If-None-Match`
- Cache-Control

This reduces unnecessary API responses when course data hasn't changed.

### 4. CDN

Static course content and large assets can be served through a CDN such as CloudFront or Cloudflare.

### 5. Monitoring

At larger scale, I would add:

- Firebase Crashlytics
- OpenTelemetry
- API latency monitoring
- Application performance monitoring
- Structured logging

---

# 🍎 iOS / macOS Strategy

The architecture was intentionally kept independent enough to support another platform.

A possible iOS implementation would use:

### UI

**SwiftUI**

### Local Storage

**SwiftData** or Core Data

### Architecture

The same general structure:

```text
SwiftUI
   ↓
ViewModel
   ↓
Domain
   ↓
Repository
   ↓
SwiftData
```

### Security

Refresh tokens can be stored using **iOS Keychain Services**.

### Kotlin Multiplatform

The pure Kotlin `:core` module could potentially be converted into a Kotlin Multiplatform shared module.

That would allow business logic such as:

- Domain models
- Validation
- Progress calculation
- Repository algorithms

to be shared between Android and iOS.

---

# 🧪 Testing

The project includes a pure Kotlin test suite inside the `:core` module.

Tests cover:

### `CourseRepositoryTest`

- Offline fallback
- Local completion preservation
- Remote/local merge behavior
- Lesson completion toggling
- Repository error handling

### `LoginValidationTest`

- Valid emails
- Empty fields
- Invalid email formats
- Password length validation

### `CourseProgressTest`

- Empty courses
- Zero completed lessons
- Partial completion
- 100% completion
- Percentage calculation and rounding

### `AuthRepositoryTest`

- Successful authentication
- Invalid input
- Invalid credentials
- Network errors
- Session creation

Run the tests with:

```bash
./gradlew :core:test
```

The tests run on the JVM, so an Android emulator is not required.

---

# 🚀 Running the Project

## Requirements

- Android Studio Ladybug or newer
- JDK compatible with the project
- Android SDK
- Android emulator or physical Android device

## Clone the Repository

```bash
git clone <your-repository-url>
cd <project-folder>
```

Open the project in Android Studio and let Gradle finish syncing.

## Run the App

Run the `app` configuration from Android Studio.

Or build the debug APK:

```bash
./gradlew assembleDebug
```

The APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install it using ADB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

# 🔑 Demo Credentials

### Valid Login

Any valid email format with a password containing at least 6 characters.

Example:

```text
Email: student@skillforge.io
Password: password123
```

You can also use the **Autofill Valid Credentials** button on the login screen.

### Invalid Login

Use:

```text
Password: wrongpass
```

or use the **Autofill Invalid** button to test the rejection flow.

---

# 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Primary language |
| Jetpack Compose | UI |
| Material 3 | Design system |
| MVVM | Presentation architecture |
| Clean Architecture | Project structure |
| Room | Local database |
| SQLite | Local persistence |
| Kotlin Flow | Reactive data |
| StateFlow | UI state |
| Coroutines | Async operations |
| WorkManager | Future background sync |
| Gradle | Build system |
| JUnit | Unit testing |

---

# 📂 High-Level Project Structure

```text
.
├── app/
│   ├── data/
│   │   ├── local/
│   │   ├── remote/
│   │   └── repository/
│   │
│   ├── presentation/
│   │   ├── login/
│   │   ├── dashboard/
│   │   └── course/
│   │
│   └── ...
│
├── core/
│   ├── domain/
│   ├── repository/
│   ├── validation/
│   └── test/
│
└── README.md
```

---

# 🎯 Project Goals

This project was built to demonstrate more than just a Compose UI.

The main focus was on:

- Clean architecture
- Offline-first data handling
- Reactive UI with Flow
- Local database design
- Repository patterns
- Testable business logic
- Practical error handling
- Responsive Material 3 UI
- Production-oriented scalability

---



## 👨‍💻 Author

**Prashant Sharma**

React Native / Android Developer / IOS Developer 
Kotlin • Jetpack Compose • React Native • TypeScript • AI/LLM Integration

---

⭐ If you find the project useful, feel free to star the repository.
