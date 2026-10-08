# Learning Dashboard (Android / Kotlin Jetpack Compose)

A production-grade, offline-first Android Learning Dashboard application built with **Kotlin**, **Jetpack Compose (Material 3)**, **MVVM + Clean Architecture**, and **Room Database**.

---

## 📱 Features & User Experience Highlights

- **✨ Fantastic UI/UX**: Custom Material 3 design system featuring Indigo & Electric Cyan branding, subtle elevation, soft borders, and responsive light/dark modes.
- **⚡ Quick Demo Shortcuts**: One-tap buttons to autofill valid (`alex.johnson@skillforge.io`) or invalid credentials (`wrongpass`) for instant evaluator testing.
- **🌐 Dynamic Offline Simulator Toggle**: A live switch in the Dashboard TopAppBar (`🌐 Online` ⟷ `📴 Offline`) to simulate network cuts on-the-fly without editing code or toggling airplane mode.
- **📊 Hero Learning Momentum Card**: Real-time progress gauge displaying overall completion percentage, enrolled courses, and completed lesson counts.
- **🔍 Search & Filter Chips**: Live search by course name or instructor, plus category chips (`All Courses`, `In Progress`, `Completed`).
- **✨ Skeleton Shimmer Loading**: Polished animated shimmer placeholder cards for seamless loading transitions instead of generic spinners.
- **🎯 Interactive Lesson Completion**: Instant reactive toggle on lesson rows with status pill transition (`○ Pending` ➔ `✓ Completed`), smooth progress updates, and a 100% completion celebration banner.

---

## 🚀 How to Run

### 1. Run on Device / Emulator
Open the project in **Android Studio (Ladybug or newer)** and run the `app` configuration, or install the debug APK:
```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 2. Run Pure Kotlin Domain & Repository Unit Tests
All business logic runs without an emulator in < 2 seconds:
```bash
./gradlew :core:test
```

### 3. Demo Credentials
- **Valid Login**: Any valid email format (e.g. `student@skillforge.io`) with password of 6+ characters. *(Or tap "Autofill Valid Credentials" on the login screen)*.
- **Simulate Rejection**: Use password `wrongpass` *(or tap "Autofill Invalid")*.

---

## 📝 Answers to Technical Evaluation Questions

### 1. Architecture: Why did you choose your architecture?
We chose **MVVM with Clean Architecture principles** and unidirectional data flow (UDF):
- **UI Layer (`app`)**: Jetpack Compose screens consuming immutable `StateFlow<UiState>` emitted by ViewModels. Screens are purely declarative and react to state.
- **Domain & Repository Layer (`:core`)**: Pure Kotlin/JVM module with zero Android framework dependencies. Contains domain entities (`Course`, `Lesson`), business rules (progress derivation, validation), and repository contracts (ports).
- **Data Layer (`app`)**: Local **Room Database** acts as the **Single Source of Truth (SSOT)**. The repository fetches remote data, merges it with local progress, and updates Room. The UI only ever observes Room via reactive `Flow`.
- **Modularity & Testability**: Decoupling domain logic into `:core` allows unit tests to execute in milliseconds on the JVM without mocking Android platform classes, and provides a clear path for code sharing with iOS via **Kotlin Multiplatform (KMP)**.

---

### 2. Offline Support: How are you storing and loading offline data?
- **Room Database Cache**: Courses and lessons are persisted in normalized SQLite tables (`courses`, `lessons`).
- **Reactive Streaming (`Flow`)**: ViewModels observe Room via `Flow`. Any database mutation (from remote sync or local lesson completion) automatically pushes updated state to the UI.
- **Local-First Write Strategy**: Marking a lesson complete updates Room immediately inside a `Mutex` lock, ensuring zero UI latency and full offline functionality.
- **Progress Preservation Merge Rule**: When refreshing from the API while online, the repository merges remote catalog updates with locally completed lesson IDs (`keepLocalCompletions`), preventing the server from overwriting client-side progress.
- **Graceful Degradation**: If network refresh fails, the app falls back to cached data seamlessly and displays an offline status banner.

---

### 3. Security: Where would you store authentication tokens in a production application?
1. **Short-Lived Access Tokens**: Stored exclusively in volatile memory (within an in-memory session manager or singleton) to minimize exposure to disk dumping.
2. **Long-Lived Refresh Tokens**: Stored encrypted in **Android Keystore-backed storage** using AES-256-GCM encryption keys (via Google Tink or Jetpack DataStore wrapped with Android KeyStore hardware-backed master keys). Plain `SharedPreferences` or plaintext databases are strictly avoided.
3. **Transport Security**: Enforce TLS 1.3 with HTTPS, Certificate Pinning (via OkHttp `CertificatePinner`), and automated 401 token refresh via an OkHttp `Authenticator`.

---

### 4. Scale: If this application had 1 million users + hundreds of courses, mention 3–5 improvements:
1. **Catalog Pagination & On-Demand Lesson Fetching**: Replace full catalog fetching with Jetpack Paging 3 (`RemoteMediator` + Room). Only load high-level course metadata on the dashboard, and fetch detailed lesson trees on-demand when opening the Course Details screen.
2. **Reliable Offline Outbox & Event-Driven Sync**: Implement a local SQLite outbox queue with **WorkManager** to record lesson completion events with idempotent UUIDs and timestamps. Synchronize with the backend using exponential backoff and batch endpoints.
3. **HTTP Conditional Requests & Cache-Control**: Use `ETag` and `If-None-Match` headers so unchanged course catalogs return `304 Not Modified`, saving server bandwidth and client battery.
4. **CDN Edge Caching**: Distribute static course assets and metadata globally via Cloudflare / CloudFront CDN edge servers.
5. **Observability & Performance Tracking**: Integrate OpenTelemetry, Firebase Crashlytics, and network latency monitoring to track API SLAs at scale.

---

### 5. Second Platform: How would you implement this on iOS/macOS?
1. **UI Layer**: Built with **SwiftUI** using `@Observable` ViewModels (iOS 17+) or `ObservableObject` with `@Published` properties.
2. **Local Storage**: Use **SwiftData** (or CoreData) with `@Query` macros for reactive database observations similar to Room's `Flow`.
3. **Architecture**: Implement the same MVVM + Repository pattern. Repositories expose Swift `AsyncSequence` or Combine publishers.
4. **Security**: Store refresh tokens in the hardware-backed **iOS Keychain Services** with `kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly`.
5. **Logic Reuse via KMP**: Because our `:core` module is pure Kotlin with no Android dependencies, it can be compiled directly into an Apple XCFramework via Kotlin Multiplatform, sharing 100% of domain models, validation, and repository algorithms across iOS and Android.

---

## 🧪 Unit Test Suite Summary

The pure Kotlin `:core` test suite includes comprehensive tests for:
- `CourseRepositoryTest`: Tests offline fallback, local completion preservation on sync, toggle actions, and error handling.
- `LoginValidationTest`: Tests valid emails, blank inputs, malformed regex patterns, and password length constraints.
- `CourseProgressTest`: Tests percentage calculation edge cases (0 lessons, 100% completion, rounding).
- `AuthRepositoryTest`: Tests successful auth session creation, invalid input rejection, credential failures, and network errors.

Run tests anytime with:
```bash
./gradlew :core:test
```
