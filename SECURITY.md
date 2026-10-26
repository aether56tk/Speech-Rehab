# Security and privacy notes

## Current risk boundary
The current prototype is not ready for real patient or child records. The UI stores all data in browser localStorage; there are no accounts, roles, server authorization, encryption-at-rest guarantee, audit log, or protected cloud backups.

## Minimum controls before real clinical use
- Authenticate all users and enforce role/record access on the server, not only in the UI.
- Define who owns a client record and which therapist/student/parent can access it.
- Use consent and appropriate authorization for any child/family data.
- Minimize personal data; prefer client codes when practical.
- Decide retention, correction, export, and deletion policies.
- Protect backups; warn before export/import/replace, and consider encryption outside the current prototype.
- Audit access and consequential changes in a backend that cannot be altered silently by the client.
- Review applicable institutional rules and legal obligations before launch.

## Import boundary
Backup import must validate a recognized format and expected arrays, normalize fields, and require explicit confirmation before replacing the currently stored dataset. JSON is untrusted input and should never be executed as code.

## Clinical safety
The app is for organization and documentation. Goal/technique/activity libraries are examples, not diagnosis or autonomous treatment instructions. Only record values actually observed or entered by the clinician; never invent trial counts, percentages, or clinical improvement.

## Current limitations
These notes are a checklist, not a formal security audit or regulatory compliance claim.
