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

## Run without Android Studio
Open the GitHub Pages URL in a browser. For local testing, serve the repository root through any static HTTP server. The app uses browser localStorage, which is scoped to the browser and origin.

## Product boundary
This is a rehabilitation management prototype, not an assessment, diagnostic, or autonomous treatment system. Goal/technique/activity recommendations are examples; a qualified clinician must review and individualize them. Use fictional data only until authentication, authorization, encryption, secure persistence, backup protection, and a suitable privacy review have been implemented.

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
4. Add proper sign-in and server-side persistent storage only after privacy and access rules are designed.
