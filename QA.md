# QA plan

Inspired by gstack's distinction between plan, review, QA, and release evidence, this file separates expected behavior from assumptions. It does not claim live browser testing unless a run is recorded.

## Smoke checks
- [ ] Root page loads; all primary navigation items reveal their own section.
- [ ] Add, edit, search, and delete a client.
- [ ] Deleting a client removes the client-linked tasks, responses, and diary notes after confirmation.
- [ ] Add an activity to a selected client; task appears in session list and dashboard.
- [ ] Mark an activity complete; completion counter and progress bar update.
- [ ] Record each response type and prompt level; response appears in therapy diary.
- [ ] Add a diary note and confirm it is client-linked.
- [ ] Export JSON backup, import the backup, and confirm counts/records round-trip.
- [ ] Invalid JSON or missing required arrays is rejected without replacing current data.
- [ ] Export responses CSV with commas and quotes in notes and verify quoting.
- [ ] Test mobile width and keyboard navigation.
- [ ] Test offline shell after first successful load; do not assume writes sync remotely.

## Evidence levels
- Static source inspection proves only that code or markers exist.
- Automated script tests prove the code paths exercised by that script.
- A real browser session is required to claim actual interaction works.
- Deployment is confirmed only from GitHub Pages deployment status and a live-page check.

## Manual test data
Use clearly fictional clients such as “Demo Client A”; do not use real personal or clinical details.
