# Product brief

## Product
Speech Rehab Management — a browser-first rehabilitation workflow organizer.

## Users
- **Therapist:** manages clients, selects and edits rehabilitation goals, plans activities, records responses, and reviews the therapy diary.
- **Student clinician:** prepares session drafts and records supervised practice.
- **Parent/caregiver (future milestone):** sees assigned home-practice activities and submits practice feedback after identity and consent controls exist.

## Core job
Help the user move from a selected communication goal to a reviewed session plan, recorded response, and organized diary entry without performing assessment or diagnosis.

## Core journey
1. Select or create a client profile.
2. Choose a communication goal from an editable library.
3. Review the suggested target, techniques, and activities.
4. Add activities to a session plan and set duration.
5. During the session, record response category, prompt level, optional trial count, and notes.
6. Add a session diary entry.
7. Review completion and recorded history; export a backup when needed.

## Product principles
- **Clinician stays in control:** suggestions are drafts, never automatic clinical decisions.
- **No fabricated outcomes:** never infer a trial count, accuracy, diagnosis, or progress.
- **Simple and responsive:** primary tasks work on narrow mobile screens and desktop.
- **Clear data ownership:** show when records are local-only and provide export/restore.
- **Privacy by design:** no real identifiable clinical data until authentication, authorization, secure storage, and consent are designed and reviewed.

## Non-goals for this prototype
- Clinical assessment, screening, diagnosis, prognosis, or automated therapy prescription.
- Patient-facing AI advice.
- Cloud synchronization or multi-user access before secure authentication and authorization.
- Claims of clinical effectiveness.

## MVP acceptance criteria
- A user can add, edit, search, and delete a client.
- Activities and notes are attached to the selected client.
- Users can record a response without entering a made-up trial count.
- JSON export/import round trips the dataset without requiring manual editing.
- Response CSV export handles commas and quotes correctly.
- Local-only storage and clinical limitations remain visible.
- Navigation remains usable at mobile width.
