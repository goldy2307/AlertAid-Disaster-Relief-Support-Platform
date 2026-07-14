/**
 * nav-auth.js — single source of truth for header auth state across AlertAid.
 * Replaces the ~30 copy-pasted inline <script> blocks that previously lived
 * in every page (citizen_dashboard.html, login.html, alerts.html, etc).
 *
 * Include once, right before </body>, after role-router.js:
 *   <script src="role-router.js"></script>
 *   <script src="nav-auth.js"></script>
 *
 * Requires these optional elements to exist (any/all may be omitted):
 *   #navAuthLink       - the Sign In / Dashboard button in the nav
 *   #userOnlyOptions    - element(s) shown only when logged in
 *   #guestOnlyOptions   - element(s) shown only when logged out
 *
 * Exposes on window: checkAuthStatus, resolveDashboardHref, signOut, getValidToken
 */
(function (w, d) {
  'use strict';

  function getValidToken() {
    try {
      const token = localStorage.getItem('token');
      if (!token || token === 'null' || token === 'undefined') return null;
      // best-effort expiry check (mirrors auth-client.js, kept dependency-free here)
      const parts = token.split('.');
      if (parts.length === 3) {
        const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
        if (payload.exp && payload.exp < Math.floor(Date.now() / 1000)) return null;
      }
      return token;
    } catch { return null; }
  }

  const DASHBOARD_BY_ROLE = {
    ADMIN: 'admin_dashboard.html',
    VOLUNTEER: 'volunteer_dashboard.html',
    ORG: 'organization_dashboard.html',
    ORGANIZATION: 'organization_dashboard.html',
    USER: 'citizen_dashboard.html',
    CITIZEN: 'citizen_dashboard.html'
  };

  function resolveDashboardHref() {
    const routerRole = (w.RoleRouter && typeof w.RoleRouter.getRole === 'function')
      ? w.RoleRouter.getRole() : null;
    let storedRole = null;
    try { storedRole = localStorage.getItem('role') || localStorage.getItem('alertaid.role'); } catch {}
    const hint = (w.__ALERTAID_ROLE__ || routerRole || storedRole || '').toString().toUpperCase();
    return DASHBOARD_BY_ROLE[hint] || 'citizen_dashboard.html';
  }

  function checkAuthStatus() {
    const token = getValidToken();
    const userOptions = d.getElementById('userOnlyOptions');
    const guestOptions = d.getElementById('guestOnlyOptions');
    const navAuthLink = d.getElementById('navAuthLink');

    if (token) {
      if (userOptions) userOptions.style.display = 'flex';
      if (guestOptions) guestOptions.style.display = 'none';
      if (navAuthLink) {
        navAuthLink.innerHTML = '<i class="fas fa-tachometer-alt"></i> Dashboard';
        navAuthLink.href = resolveDashboardHref();
      }
    } else {
      if (userOptions) userOptions.style.display = 'none';
      if (guestOptions) guestOptions.style.display = 'block';
      if (navAuthLink) {
        navAuthLink.innerHTML = '<i class="fas fa-sign-in-alt"></i> Sign In';
        navAuthLink.href = 'login.html';
      }
    }
  }

  function clearAuth() {
    try {
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      localStorage.removeItem('email');
      localStorage.removeItem('alertaid.role');
      sessionStorage.clear();
    } catch {}
  }

  function signOut() {
    const doSignOut = () => { clearAuth(); w.location.replace('login.html'); };
    if (typeof w.showConfirm === 'function') {
      w.showConfirm('Are you sure you want to sign out?', (confirmed) => { if (confirmed) doSignOut(); });
    } else if (confirm('Are you sure you want to sign out?')) {
      doSignOut();
    }
  }

  // expose globally — pages currently call these via onclick="signOut()" etc.
  w.getValidToken = getValidToken;
  w.resolveDashboardHref = resolveDashboardHref;
  w.checkAuthStatus = checkAuthStatus;
  w.signOut = signOut;

  d.addEventListener('DOMContentLoaded', checkAuthStatus);
})(window, document);
