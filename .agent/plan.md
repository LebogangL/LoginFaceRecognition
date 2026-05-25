# Project Plan

A facial recognition app for login purposes. The app allows users to register their faces and then use face recognition to log in. It uses ML Kit for face detection and TensorFlow Lite with a FaceNet model for face recognition. The app follows Material Design 3 guidelines, has a vibrant and energetic color scheme, and supports edge-to-edge display. It includes a registration screen, a login screen, and a home screen accessible after successful login.

The user is currently seeing 9 errors in build.gradle.kts related to dependency management (Version Catalog).

## Project Brief

# Project Brief: LoginFaceRecognition

## Features
1. **Face Enrollment & Registration**: Allows users to register their identity by capturing their facial features using the device camera. The app processes the image to extract unique embeddings for secure storage.
2. **Facial Recognition Login**: Provides a seamless authentication experience by matching the user's live face against registered data in real-time, ensuring secure access.
3. **Real-time Face Detection Overlay**: Utilizes ML Kit to provide immediate visual feedback (bounding boxes) during scanning, ensuring the user's face is correctly positioned.
4. **Post-Authentication Home Screen**: A vibrant, Material 3-compliant landing page that is only accessible after a successful facial match.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3 with Edge-to-Edge display)
- **Asynchronous Logic**: Kotlin Coroutines & Flow
- **Camera Integration**: Jetpack CameraX
- **Face Detection**: Google ML Kit (Face Detection API)
- **Face Recognition**: TensorFlow Lite (FaceNet model)
- **Code Generation**: KSP (Kotlin Symbol Processing)

## Implementation Steps

### Task_1_FixDependencies_CoreSetup: Fix the 9 dependency errors in the Version Catalog and build.gradle.kts. Configure project dependencies for ML Kit, TFLite, and CameraX. Initialize the Room database for storing face embeddings and set up the Navigation graph.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Project builds successfully without dependency errors
  - Room database and DAOs for face embeddings are defined
  - Navigation graph with Registration, Login, and Home destinations is initialized
- **StartTime:** 2026-03-25 15:14:45 SAST

### Task_2_FaceRegistration: Implement the Face Enrollment screen. Integrate CameraX with ML Kit for real-time face detection overlays. Use a FaceNet TFLite model to extract face embeddings and save them to the Room database.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Camera preview displays real-time face detection bounding boxes
  - Face embeddings are correctly extracted and stored in Room upon registration
  - Registration flow provides success/failure feedback

### Task_3_FaceLoginHome: Implement the Login screen with real-time recognition and the post-authentication Home screen. Match live face embeddings against the database and navigate to the Home screen upon a successful match.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Login screen performs real-time matching against registered users
  - Successful face recognition navigates the user to the Home screen
  - Home screen is only accessible after a successful facial match

### Task_4_Polish_RunVerify: Refine the UI with Material 3 vibrant styling and Edge-to-Edge support. Create an adaptive app icon. Perform a final Run and Verify to ensure stability. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Material 3 theme with vibrant colors and Edge-to-Edge implemented
  - Adaptive app icon is configured
  - make sure all existing tests pass
  - build pass
  - app does not crash

