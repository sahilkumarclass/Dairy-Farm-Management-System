# DairySmart Pro — Android (Owner/Staff admin app)

Native Kotlin Android app for farm **owners and staff**. It talks to the existing
Spring Boot API (`../api`) and mirrors the `web/` admin feature set: dashboard,
customers, milk entries, expenses, bills, herd, and staff — with the same
OWNER/STAFF role gating and three languages (English, Hindi, Punjabi).

## Stack

- Kotlin + Jetpack Compose (Material 3)
- MVVM + Hilt (DI) + Coroutines/Flow
- Retrofit + OkHttp + Moshi, Paging 3 for list endpoints
- DataStore for the auth token, AndroidX per-app locales for language
- minSdk 26, target/compileSdk 35

## Project layout

```
app/src/main/java/com/sahilkumar/dfms/
  core/network/   Retrofit ApiService, OkHttp auth interceptor, Moshi, Page wrapper
  core/data/      SessionManager (token + role), AuthRepository
  core/ui/        Material3 theme, reusable components, LocaleManager, SessionViewModel
  core/util/      formatters, validation, paging source, error mapping
  feature/        login, dashboard, customers, milk, expenses, bills, herd, staff
  model/          DTOs mirroring the API
  nav/            role-aware drawer + NavHost
res/values{,-hi,-pa}/strings.xml   English / Hindi / Punjabi
```

## Configure the API URL

The base URL is a `BuildConfig` field (see `app/build.gradle.kts`):

- **Debug** → `http://10.0.2.2:8080` (the Android *emulator's* alias for your
  computer's `localhost`, where the API runs on port 8080).
- For a **physical device**, change the debug `API_BASE_URL` to your machine's
  LAN IP, e.g. `http://192.168.1.20:8080`, and make sure the phone is on the
  same network. (Cleartext HTTP is allowed in debug via the manifest.)

## Build & run

Prerequisites: Android SDK (platform 35) and a JDK. From this folder:

```bash
# Build the debug APK
./gradlew :app:assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Install on a running emulator/device
./gradlew :app:installDebug
```

Or open the `android/` folder in Android Studio and press Run.

Start the backend first (`../api`, port 8080, Postgres on 5433). Sign in with an
**OWNER** or **STAFF** account — CUSTOMER logins are rejected (use the customer
portal instead). Switch languages from the drawer ▸ Language.

## Notes

- `local.properties` (SDK path) is machine-specific and git-ignored.
- STAFF sees Dashboard, Customers, Milk; OWNER additionally sees Expenses,
  Bills, Herd, Staff — matching the web admin.
- Phone fields enforce exactly 10 digits, matching the backend validation.
