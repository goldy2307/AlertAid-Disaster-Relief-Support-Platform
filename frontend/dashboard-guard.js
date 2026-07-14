import { isTokenExpired, normalizeToken } from './auth-client.js';

const ROLE_MAP = {
  ADMIN: 'admin',
  VOLUNTEER: 'volunteer',
  ORG: 'org',
  CITIZEN: 'user'
};

function clearAuth() {
  try {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('email');
    localStorage.removeItem('alertaid.role');
    sessionStorage.clear();
    document.cookie.split(';').forEach(cookie => {
      const eq = cookie.indexOf('=');
      const name = (eq > -1 ? cookie.substr(0, eq) : cookie).trim();
      if (name) {
        document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;`;
      }
    });
  } catch (err) {
    console.warn('Failed to clear auth state', err);
  }
}

function mapRoleForRouter(role) {
  if (!role) return null;
  return ROLE_MAP[role.toUpperCase()] || null;
}

const PUBLIC_PAGES = new Set(['report', 'report.html']);

function isPublicPage() {
  try {
    const path = (window.location.pathname || '').split('?')[0].split('#')[0];
    const segment = path.split('/').filter(Boolean).pop() || '';
    const normalized = segment.toLowerCase();
    const withoutExt = normalized.replace(/\.html$/, '');
    return PUBLIC_PAGES.has(normalized) || PUBLIC_PAGES.has(withoutExt);
  } catch {
    return false;
  }
}

if (!isPublicPage()) {
  (function enforceJwt() {
    const token = normalizeToken(localStorage.getItem('token'));
  if (!token || isTokenExpired(token)) {
    clearAuth();
    window.location.replace('login.html');
    return;
  }

  const storedRole = localStorage.getItem('role');
  const mapped = mapRoleForRouter(storedRole);
  if (mapped) {
    if (window.RoleRouter && typeof window.RoleRouter.setRole === 'function') {
      window.RoleRouter.setRole(mapped);
    }
    try { localStorage.setItem('alertaid.role', mapped); } catch {}
  }
  })();
}
