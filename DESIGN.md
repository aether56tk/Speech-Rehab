# Design system

## Direction
Calm, trustworthy, practical clinical-workspace UI. Use teal as the primary action color, pale green for selected states, off-white page backgrounds, white cards, and readable dark text. Avoid over-decorated illustrations and competing accent colors in the core workspace.

## Tokens
- Primary: `#087f67`
- Primary subtle: `#dff5ed`
- Background: `#f4f8f7`
- Text: `#172b2a`
- Muted text: `#657875`
- Borders: `#dce8e5`
- Warning/confirmation must use text in addition to color.

## Layout
- Desktop: compact left navigation and flexible main content with consistent spacing.
- Mobile: content-first layout with bottom navigation and full-width controls.
- Cards: consistent radius, clear heading hierarchy, and no excessive nested card surfaces.
- Forms: explicit labels; placeholders are examples, not labels.
- Empty states: explain the next action.
- Destructive actions: confirm impact; cascade through related data deliberately.

## Interaction rules
- Every visible button should perform a real action.
- Keep focus visible and support keyboard navigation.
- Use confirmations for destructive and replacement operations.
- Use polite status messages for save/export/import success or failure.
- Do not rely on color alone to communicate completion or response status.
- Avoid a visual representation that implies data is securely synced when it is only in localStorage.

## Content voice
Use plain language. Prefer “Communication goal,” “Suggested techniques,” “Activities,” “Record response,” and “Therapy diary.” Use “suggested” rather than “prescribed.”
