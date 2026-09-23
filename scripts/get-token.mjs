#!/usr/bin/env node
// Obtiene un JWT de Neon Auth (Better Auth) sin frontend e imprime
// session.access_token, listo para usar como "Authorization: Bearer <token>".
//
// Uso (PowerShell):
//   $env:NEON_AUTH_URL = "https://ep-...neonauth.sa-east-1.aws.neon.tech/foliobuilderdevbd"
//   $env:NEON_TEST_EMAIL = "tu@correo.com"
//   $env:NEON_TEST_PASSWORD = "tu-clave"
//   node scripts/get-token.mjs
//
// Alternativa: node scripts/get-token.mjs <base-url> <email> <password>

const baseUrl = (process.env.NEON_AUTH_URL || process.argv[2] || '').replace(/\/+$/, '');
const email = process.env.NEON_TEST_EMAIL || process.argv[3];
const password = process.env.NEON_TEST_PASSWORD || process.argv[4];

if (!baseUrl || !email || !password) {
  console.error('Uso: node scripts/get-token.mjs <base-url> <email> <password>');
  console.error('  o seteando NEON_AUTH_URL / NEON_TEST_EMAIL / NEON_TEST_PASSWORD');
  process.exit(1);
}

const endpoint = `${baseUrl}/auth/sign-in/email`;
const response = await fetch(endpoint, {
  method: 'POST',
  headers: { 'content-type': 'application/json' },
  body: JSON.stringify({ email, password }),
});

const body = await response.json();

if (!response.ok || body.error || !body.data?.session?.access_token) {
  console.error('Sign-in fallo:', response.status);
  console.error(body?.message || body?.error || body);
  process.exit(1);
}

const token = body.data.session.access_token;
console.log(token);

try {
  const { spawnSync } = await import('node:child_process');
  const cmd = process.platform === 'win32' ? 'clip' : 'pbcopy';
  const result = spawnSync(cmd, { input: token });
  if (!result.error) console.error('(token copiado al portapapeles)');
} catch {
  // Sin portapapeles disponible: solo se imprimio el token.
}