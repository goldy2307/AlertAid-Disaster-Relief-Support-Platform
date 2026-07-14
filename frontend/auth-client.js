// Simple fetch wrapper that adds Authorization header from localStorage
export function normalizeToken(raw) {
  if (!raw) return null;
  const trimmed = raw.trim();
  if (!trimmed || trimmed === 'null' || trimmed === 'undefined') return null;
  return trimmed;
}

export function isTokenExpired(token) {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return true;
    const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
    if (!payload.exp) return true;
    const nowSec = Math.floor(Date.now() / 1000);
    return payload.exp < nowSec;
  } catch { return true; }
}

export async function apiFetch(path, options = {}) {
  let token = normalizeToken(localStorage.getItem('token'));
  if (!token) {
    try { localStorage.removeItem('token'); } catch {}
  } else if (isTokenExpired(token)) {
    // clear expired token to avoid phantom signed-in state
    try { localStorage.removeItem('token'); } catch {}
    token = null;
  }
  const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
  if (token) headers['Authorization'] = `Bearer ${token}`;
  const res = await fetch(path, { ...options, headers });
  if (res.status === 401 && token) {
    // redirect to login only if we thought the user was authenticated
    window.location.href = 'login.html';
    return Promise.reject(new Error('Unauthorized'));
  }
  return res;
}
