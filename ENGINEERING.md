# Engineering plan

## Current architecture
- Static HTML/CSS/JavaScript; no build system or runtime package dependency.
- Browser localStorage persists one versioned JSON object for the current origin/browser.
- JSON backup includes clients, tasks, responses, and diary notes.
- CSV export is intended for analysis and recordkeeping, not a clinical evaluation.
- Service worker caches the static shell for basic offline loading.

## Current constraints
- Local storage does not synchronize across devices, provide user identity, or offer meaningful access control.
- Any person with access to the browser profile can access local data.
- Local backups are unencrypted files.
- The app has not yet been validated through an automated real-browser flow.
- The current single-file implementation should be split into modules as features grow.

## Recommended architecture steps
1. **Stabilize behavior:** add import validation, export/restore flow, referential integrity, and deterministic smoke tests.
2. **Refactor:** split data model, persistence adapter, goal catalog, views, and export/import utilities.
3. **Add browser QA:** exercise create/edit/delete, planning, completion, response records, diary, backup round-trip, and mobile navigation.
4. **Introduce server storage only after permissions:** select a backend, define therapist/student/parent relationships, and enforce every access rule server-side.
5. **Security review:** consent/retention, child data, least privilege, audit history, secure backup handling, and deletion workflows.
6. **Optional PWA polish:** proper icons, install prompts, versioned cache invalidation, offline data expectations, and update notifications.

## Data entities
- Client: id, display name/code, age text, primary concern note, additional note.
- Activity: id, client id, title, goal, duration, completion status.
- Response: id, client id, activity label, response category, prompt level, optional trial text, notes, date.
- Diary note: id, client id, session title, narrative note, date.

## Non-functional requirements
- Use stable IDs; validate imported data at the boundary.
- Do not compute accuracy unless numerator and denominator are valid, explicitly recorded values.
- Do not render untrusted text through HTML without escaping.
- Preserve an export path and communicate storage limitations.
- Errors should be actionable and not expose secrets.
