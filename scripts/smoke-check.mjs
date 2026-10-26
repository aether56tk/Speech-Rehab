import { readFileSync } from 'node:fs';
import assert from 'node:assert/strict';

const html = readFileSync(new URL('../index.html', import.meta.url), 'utf8');
const scriptMatch = html.match(/<script>([\s\S]*?)<\/script>/i);
assert.ok(scriptMatch, 'index.html should contain the application script');
new Function(scriptMatch[1]);
console.log('PASS: app JavaScript parses');
const clinicJs = readFileSync(new URL('../clinic.js', import.meta.url), 'utf8');
new Function(clinicJs);
console.log('PASS: clinic workspace JavaScript parses');

const required = [
  'function importBackup()',
  "parsed?.format==='speech-rehab-backup-v1'",
  "confirm('Import this backup",
  "else if(a==='import-backup')importBackup()",
  "db.responses=db.responses.filter(r=>r.clientId!==id)",
  "db.diary=db.diary.filter(n=>n.clientId!==id)",
  'function requireClients()',
  'function exportResponsesCsv()',
  'function recordTask(id)',
  'function addDiary()',
  'field(\'technique\',\'Technique / cueing approach (optional)\'',
  'technique:String(x.technique||\'\')',
  'function addDiary(clientId=null)',
  'data-action="view-client"',
  'function clientDetailHtml(id)',
  'function editResponse(id)',
  'function editDiary(id)',
  "else if(a==='delete-response')",
  "else if(a==='delete-diary')",
  'id="auth-gate"',
  'async function syncWorkspace()',
  'async function applySession(session)',
  'SUPABASE_PUBLISHABLE_KEY',
  "from('user_workspaces').upsert",
  'function renderClinicTools',
  'create_speech_rehab_invitation',
  'accept_speech_rehab_invitation',
];
for (const marker of required) assert.ok(html.includes(marker), 'missing app contract: ' + marker);
console.log('PASS: backup, client profiles, session, response, diary, auth, and cloud-sync contracts are present');

assert.ok(html.includes('localStorage'), 'prototype persistence boundary should be explicit in source');
assert.ok(html.includes('serviceWorker.register'), 'PWA service worker should be registered');
console.log('PASS: local persistence and offline shell hooks are present');
