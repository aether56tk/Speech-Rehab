import { readFileSync } from 'node:fs';
import assert from 'node:assert/strict';

const html = readFileSync(new URL('../index.html', import.meta.url), 'utf8');
const scriptMatch = html.match(/<script>([\\s\\S]*?)<\\/script>/i);
assert.ok(scriptMatch, 'index.html should contain the application script');
new Function(scriptMatch[1]);
console.log('PASS: app JavaScript parses');

const required = [
  'function importBackup()',
  "parsed?.format==='speech-rehab-backup-v1'",
  "confirm('Import this backup",
  "else if(a==='import-backup')importBackup()",
  "db.responses=db.responses.filter(r=>r.clientId!==id)",
  "db.diary=db.diary.filter(t=>t.clientId!==id)",
  'function requireClients()',
  'function exportResponsesCsv()',
  'function recordTask(id)',
  'function addDiary()',
];
for (const marker of required) assert.ok(html.includes(marker), 'missing app contract: ' + marker);
console.log('PASS: backup, client deletion, session, response, and diary contracts are present');

assert.ok(html.includes('localStorage'), 'prototype persistence boundary should be explicit in source');
assert.ok(html.includes('serviceWorker.register'), 'PWA service worker should be registered');
console.log('PASS: local persistence and offline shell hooks are present');
