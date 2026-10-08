# Architecture Documentation: Learning Dashboard

## 1. System Overview & Clean Architecture

The application is architected following **Clean Architecture** and **MVVM (Model-View-ViewModel)** with Unidirectional Data Flow (UDF).

```
+-----------------------------------------------------------------+
|                        UI (Jetpack Compose)                     |
|    LoginScreen        CoursesScreen        CourseDetailScreen   |
+--------------------------------+--------------------------------+
                                 | observes StateFlow<UiState>
                                 | sends User Intents / Events
+--------------------------------v--------------------------------+
|                        Presentation (ViewModels)                |
|    LoginViewModel     CoursesViewModel   CourseDetailViewModel  |
+--------------------------------+--------------------------------+
                                 | invokes use cases / repos
+--------------------------------v--------------------------------+
|                   Domain & Repositories (:core JVM)             |
|    AuthRepository                       CourseRepository        |
|    - Validation Rules                   - Progress Derivation   |
|    - Monotonic Merge Rules              - Concurrency Lock      |
+-----------------+-------------------------------+---------------+
                  | depends on ports (interfaces) |
+-----------------v---------------+   +-----------v---------------+
|    Remote APIs (Data Sources)   |   |   Local Persistence       |
|    MockAuthApi                  |   |   Room Database (SQLite)  |
|    MockCourseApi                |   |   RoomCourseCache         |
+---------------------------------+   +---------------------------+
```

---

## 2. Module Structure

| Module | Responsibilities | Technology & Dependencies |
|---|---|---|
| `:core` | Domain entities (`Course`, `Lesson`), Ports (`CourseApi`, `CourseCache`, `TokenStore`), business logic, and Repositories | Pure Kotlin/JVM, `kotlinx-coroutines-core`, `kotlin-test` |
| `:app` | UI Components, Jetpack Compose screens, ViewModels, Material 3 Design System, Room DB, Navigation graph, Dependency wiring | Android 15 (API 35), Jetpack Compose, Room DB, Material 3 |

---

## 3. Unidirectional Data Flow (UDF)

### A. Dashboard Data Flow (SSOT Pattern)
1. `CoursesViewModel` initializes and calls `repo.refreshCourses()`.
2. `CourseRepository` requests remote course metadata via `api.fetchCourses()`.
3. If successful, remote data is merged with locally completed lesson states via `keepLocalCompletions` and written into **Room Database**.
4. Room automatically emits the updated dataset through reactive `Flow<List<Course>>`.
5. `CoursesViewModel` combines the room stream with filter and search states into an immutable `UiState` rendered by `CoursesScreen`.

### B. Lesson Completion Flow
1. User taps a lesson item in `CourseDetailScreen`.
2. `CourseDetailViewModel.toggleLesson(lessonId)` is triggered.
3. `CourseRepository.toggleLesson(courseId, lessonId)` acquires a `Mutex` lock, updates the lesson entity in Room, and saves.
4. Room emits the new state, immediately updating both `CourseDetailScreen` and `CoursesScreen` without requiring any network round-trip.

---

## 4. Offline Persistence & Resilience

- **Local-First Single Source of Truth**: The UI reads exclusively from Room. The network is treated solely as a data synchronization channel.
- **Monotonic Progress Preservation**: When an internet connection is re-established and `refreshCourses()` runs, the repository guarantees that completed lesson IDs are never overridden or lost.
- **Dynamic Simulation Toggle**: A runtime switch in the Dashboard enables immediate offline mode simulation for stress testing and evaluator demos.

---

## 5. Security Architecture (Production Blueprint)

In a production environment:
- **Authentication**: JWT Access Token (in-memory) + Refresh Token (hardware-backed Android Keystore with AES-256-GCM encryption).
- **Network Security**: HTTPS TLS 1.3, OkHttp `CertificatePinner`, and automated token rejuvenation with OkHttp `Authenticator`.
- **Integrity**: ProGuard/R8 obfuscation, SafetyNet/Play Integrity API verification.

---

## 6. Scaling Strategies (1M+ Users)

1. **Pagination**: Implement Android Jetpack Paging 3 with `RemoteMediator` for infinite scrolling of catalogs.
2. **Outbox Pattern**: Local SQLite sync queue managed with `WorkManager` for guaranteed at-least-once delivery of lesson progress.
3. **HTTP Cache Controls**: `ETag` / `If-None-Match` for `304 Not Modified` payload reduction.
4. **Edge CDN Distribution**: Cache course structure at edge nodes globally.

---

## 7. Cross-Platform Strategy (iOS/macOS)

- **UI**: SwiftUI declarative views + `@Observable` ViewModels.
- **Persistence**: SwiftData / CoreData with `@Query`.
- **Security**: Keychain Services with `kSecAccessControlBiometryAny` / `kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly`.
- **Code Sharing**: The pure Kotlin `:core` module is ready for KMP compilation into an XCFramework for native iOS import.
