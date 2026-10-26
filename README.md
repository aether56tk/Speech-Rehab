# Speech Rehab

An Android starter app for speech rehabilitation management. It helps therapists, parents, and student clinicians organize client profiles, therapy goals, activities, sessions, response notes, and progress.

## Current prototype
- Home dashboard
- Client list and Add Client form
- Goal suggestions with techniques and activities
- Daily session plan with completion checkboxes
- Response recording with response type, prompt level, optional notes and trial counts
- Simple progress view

## Limitations
Data is stored in memory only and resets when the app process restarts. Authentication, secure persistent storage, role-based access, and cloud sync are not implemented. Use fictional demo data only. This app does not diagnose or perform assessments. All suggestions require clinician review.

## Run
1. Open this repository folder in Android Studio.
2. Allow Gradle sync to finish and install Android SDK 35 if prompted.
3. Run on an emulator or Android device.

## Next steps
Add Room database persistence, client detail pages, therapy diary history, and secure role-based access.