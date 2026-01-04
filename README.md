# Cyberon

### 📥 Download
- **[v2.1 (Glassmorphism UI & Features)](https://github.com/abhi-abhi86/Cyberon/raw/main/releases/Cyberon-v2.1-debug.apk)** - Latest
- [v1.0 (Initial Release)](https://github.com/abhi-abhi86/Cyberon/raw/main/releases/Cyberon-v1.0.apk)

### 📸 Features
- **Glassmorphism UI**: Neon styling, liquid ripple effects.
- **UDP Discovery**: Fast local peer finding.
- **Transfer History & Settings**: Full control.



A secure, high-performance Android application that calculates file checksums (CRC32/SHA256) using a native C++ library integrated via JNI.

## Features

-   **Hybrid Architecture**: Combines Kotlin (UI) and C++ (Performance).
-   **Native Performance**: Uses NDK to perform intensive file processing in C++.
-   **Modern UI**: Built with Jetpack Compose.
-   **Thread Safety**: Offloads native calls to background threads using Coroutines.

## Tech Stack

-   **Android SDK** (Min 24, Target 34)
-   **Kotlin** + **Jetpack Compose**
-   **C++ 17** (NDK)
-   **CMake** (Build system for native code)
-   **JNI** (Java Native Interface)

## Getting Started

### Prerequisites

-   Andoid Studio
-   Android SDK & NDK
-   CMake
-   Gradle (Wrapper included)

### Quick Start (Command Line)

We have provided a developer runner script to easily build and run the app.

```bash
# 1. Make the script executable (if needed)
chmod +x dev_runner.sh

# 2. Run the build & install script
./dev_runner.sh
```

### Manual Build

1.  Open the project in **Android Studio**.
2.  Allow Gradle to sync.
3.  Connect a device or emulator.
4.  Run `app` configuration.

## Usage

1.  Launch the app on your device.
2.  Tap the **"Select File"** button.
3.  Choose a file from your storage.
4.  The app will calculate the checksum using the C++ native library and display it on screen.

## Project Structure

-   `src/main/java/`: Kotlin source code (UI, Logic).
-   `src/main/cpp/`: Native C++ source code (`native-lib.cpp`).
-   `src/main/cpp/CMakeLists.txt`: Native build configuration.