# Speech Rehab Management

Speech Rehab is a browser-first, mobile-friendly management workspace for speech-language rehabilitation. It is intended to help clinicians, student clinicians, and eventually parents organize therapy goals, activities, sessions, responses, notes, and home practice.

## Live prototype
If GitHub Pages is enabled: https://aether56tk.github.io/Speech-Rehab/

## Current features
- Dashboard for client and activity overview
- Client profiles: create, edit, search, and delete
- Goal library with example targets, techniques, and activities
- Session activity planning and completion tracking
- Response recording: response type, prompt level, optional trial count and notes
- Therapy diary notes and response history
- JSON backup export and restore
- CSV export of response records
- Responsive layout and basic PWA/offline shell
- Supabase email/password sign-up and sign-in
- Private per-account cloud workspace with RLS
- Clinic creation, role-based invitations for therapists/student clinicians/parents, and single-use invitation tokens
- Clinic client records and therapy notes with explicit parent read-sharing
- Clinic database tables and RLS policy setup in `supabase/setup.sql`
- Per-account cloud workspace sync with row-level security (RLS)
- Profile creation on sign-up and a private workspace row per account
- Cloud sync status and sign-out

## Run without Android Studio
Open the GitHub Pages URL in a browser. For local testing, serve the repository root through any static HTTP server. The app now uses Supabase Auth and a private `user_workspaces` row for signed-in accounts. Local storage is retained as a local cache, but cloud data is loaded after sign-in. Supabase project URL and publishable key are configured in `index.html`; publishable keys are intended for browser use when RLS is correctly configured. Never add a service-role or secret key to the frontend.

## Product boundary
This is a rehabilitation management prototype, not an assessment, diagnostic, or autonomous treatment system. Goal/technique/activity recommendations are examples; a qualified clinician must review and individualize them. Use fictional data only until your clinic's privacy requirements and operational controls are reviewed. The app now has a separate clinic workspace module. Clinic records use role-based membership and per-client parent read access. Invitation tokens are generated in-app but must be shared manually with the invited email. The personal workspace and clinic records remain separate; existing personal demo data is not automatically migrated into clinic records. RLS restricts workspace rows to the signed-in account. This prototype is not a substitute for a full clinical-record compliance review.

## Quality process
This project uses a small, inspectable workflow inspired by gstack's separation of product planning, design review, engineering review, QA, and security review. It does not install or copy gstack runtime code.
- [Product brief](PRODUCT.md)
- [Design system](DESIGN.md)
- [Architecture and engineering plan](ENGINEERING.md)
- [Security and privacy notes](SECURITY.md)
- [QA plan](QA.md)
- [Changelog](CHANGELOG.md)

## Publishing
Enable **Settings → Pages → Deploy from a branch → main → /(root)**. The `.github/workflows/pages.yml` workflow also publishes the static site on pushes to main when GitHub Pages is configured for GitHub Actions.

## Next milestones
1. Add stable multi-record session templates and editing.
2. Add a restore/import preview and clear merge/replace options.
3. Split the prototype into maintainable modules and add automated browser tests.
4. Add clinic workspaces with explicit therapist/student/parent membership and client-level permissions.
5. Add automated browser tests, recovery flows, and a privacy/security review before real patient use.
