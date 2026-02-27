# Bliss

Bliss is an Android mental wellness assistant that helps users build healthier habits through AI-powered support and self-tracking tools.

## Features

- AI therapist chatbox
- Smart music recommendations
- Smart journal system
- Weekly mood tracking

## Tech Stack

- **Language & Build**: Java, Gradle Kotlin DSL, Android Gradle Plugin (AGP 8.12.2)
- **Android SDK**: minSdk 24, targetSdk 36, compileSdk 36
- **Architecture/UI**: Android Views + Jetpack Compose (Material 3), AndroidX Lifecycle
- **Backend & Auth/Data**: Firebase Authentication, Firestore, Storage, Analytics
- **AI Model Backend**: FastAPI (Python)
- **Networking**: Retrofit + Gson Converter
- **Media & Images**: Glide, Picasso, Cloudinary Android SDK
- **Visualization**: MPAndroidChart
- **Testing**: JUnit4, AndroidX Test (JUnit Ext, Espresso), Compose UI Test

## Architecture

- **Presentation Layer**: Activities/Fragments + Jetpack Compose screens handle UI and user interactions.
- **State & Lifecycle**: AndroidX Lifecycle components (`ViewModel`, `LiveData`) manage screen state.
- **Data Layer**: Repository-style data flow integrates Firebase (Auth, Firestore, Storage, Analytics).
- **Network/External APIs**: Retrofit clients connect app features to external services.
- **AI Integration**: The Android app calls a FastAPI backend for AI therapist/chat and recommendation-related flows.
- **Media Pipeline**: Cloudinary + Glide/Picasso are used for image upload, storage, and rendering.

## Project Structure

- Root build config: [build.gradle.kts](build.gradle.kts)
- Project settings: [settings.gradle.kts](settings.gradle.kts)
- Gradle properties: [gradle.properties](gradle.properties)
- Local machine config: [local.properties](local.properties)
- Android app module: [app/](app)

## Prerequisites

- JDK 17+
- Android Studio (latest stable)
- Android SDK installed and configured
- FastAPI backend running and reachable from the app

## Local Configuration

Set the following keys in [local.properties](local.properties):

- `sdk.dir`
- `GOOGLE_API_KEY`
- `GEMINI_API_KEY`
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`

Never commit secrets to version control.

## Getting Started

1. Clone this repository.
2. Open the project in Android Studio.
3. Configure [local.properties](local.properties) with SDK path and API keys.
4. Ensure your FastAPI backend is running.
5. Sync Gradle and run the app.

## Build Commands

From the project root:

```bash
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew clean
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Test Commands

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Useful Gradle Command

```bash
./gradlew tasks
```

## Troubleshooting

- Refresh dependencies: `./gradlew --refresh-dependencies`
- Clean build artifacts: `./gradlew clean`
- Verify SDK path and API keys in [local.properties](local.properties)
- Confirm FastAPI backend URL is reachable from the Android app
