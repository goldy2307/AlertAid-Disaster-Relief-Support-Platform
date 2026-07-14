// === RoleRouter: universal role-based navigation for AlertAid ===
// v2.0 - supports extensionless routes

(function (w, d) {
  const STORAGE_KEY = "alertaid.role";

  const ROUTE_MAP = {
    user: {
      dashboard: "citizen/dashboard",
      profile:   "profile",
      settings:  "citizen/settings",
      change_password: "change_password",
      alerts: "alerts",
      donate: "donations",
      report: "report",
      help: "helpforpeople",
      volunteer_signup: "volunteer/signup",
      campaigns: "campaigns",
      contributions: "contributions",
      login: "login"
    },
    volunteer: {
      dashboard: "volunteer/dashboard",
      profile:   "profile",
      settings:  "volunteer/settings",
      change_password: "change_password",
      alerts: "alerts",
      donate: "donations",
      report: "report",
      help: "helpforpeople",
      volunteer_signup: "volunteer/signup",
      campaigns: "campaigns",
      contributions: "contributions",
      login: "login"
    },
    org: {
      dashboard: "org/dashboard",
      profile:   "profile",
      settings:  "org/settings",
      change_password: "change_password",
      alerts: "alerts",
      donate: "donations",
      report: "report",
      help: "helpforpeople",
      campaigns: "campaigns",
      contributions: "contributions",
      login: "login"
    },
    admin: {
      dashboard: "admin/dashboard",
      profile:   "profile",
      settings:  "admin/settings",
      change_password: "change_password",
      alerts: "alerts",
      donate: "donations",
      report: "report",
      help: "helpforpeople",
      campaigns: "campaigns",
      contributions: "contributions",
      login: "login"
    }
  };
  const LEGACY_ALIASES = {
    "citizen_dashboard": "citizen/dashboard",
    "volunteer_dashboard": "volunteer/dashboard",
    "organization_dashboard": "org/dashboard",
    "admin_dashboard": "admin/dashboard",
    "citizen_profile": "profile",
    "volunteer_profile": "profile",
    "org_profile": "profile",
    "admin_profile": "profile",
    "citizen_settings": "citizen/settings",
    "volunteer_settings": "volunteer/settings",
    "org_settings": "org/settings",
    "admin_settings": "admin/settings",
    "citizen_dashboard.html": "citizen/dashboard",
    "volunteer_dashboard.html": "volunteer/dashboard",
    "organization_dashboard.html": "org/dashboard",
    "admin_dashboard.html": "admin/dashboard",
    "donation": "donations",
    "donation.html": "donations",
    "contribution": "contributions",
    "contribution.html": "contributions",
    "volunteer_signup": "volunteer/signup",
    "volunteer_signup.html": "volunteer/signup",
    "change_password": "change_password",
    "change_password.html": "change_password",
    "assigned_tasks": "assigned_tasks",
    "assigned_tasks.html": "assigned_tasks",
    "markavailabilty": "markavailabilty",
    "markavailabilty.html": "markavailabilty",
    "application_org_volunteer": "application_org_volunteer",
    "application_org_volunteer.html": "application_org_volunteer"
  };
  const HTML_TARGETS = {
    "citizen/dashboard": "citizen_dashboard.html",
    "volunteer/dashboard": "volunteer_dashboard.html",
    "org/dashboard": "organization_dashboard.html",
    "admin/dashboard": "admin_dashboard.html",
    "profile": "profile.html",
    "citizen/settings": "profile.html",
    "volunteer/settings": "profile.html",
    "org/settings": "profile.html",
    "admin/settings": "profile.html",
    "change_password": "change_password.html",
    "alerts": "alerts.html",
    "donations": "donatemoney.html",
    "donation": "donatemoney.html",
    "report": "report.html",
    "helpforpeople": "helpforpeople.html",
    "help": "helpforpeople.html",
    "volunteer/signup": "volunteer_signup.html",
    "volunteer_signup": "volunteer_signup.html",
    "campaigns": "campaigns.html",
    "contributions": "Contribution.html",
    "contribution": "Contribution.html",
    "assigned_tasks": "assigned_tasks.html",
    "markavailabilty": "markavailabilty.html",
    "application_org_volunteer": "application_org_volunteer.html",
    "login": "login.html"
  };

  const qs = (sel, parent) => (parent||d).querySelector(sel);
  const qsa = (sel, parent) => Array.from((parent||d).querySelectorAll(sel));
  const fileOf = (url) => {
    try {
      const raw = (url || w.location.pathname || '')
        .split('?')[0]
        .split('#')[0]
        .replace(/^\/+/, '');
      return raw || "";
    } catch { return ""; }
  };
  const normalizePath = (value) => {
    if (!value) return "";
    const cleaned = value.replace(/^\/+/, "").replace(/\\/g, "/").replace(/\.html$/i, "");
    const key = cleaned.toLowerCase();
    return LEGACY_ALIASES[key] || key;
  };
  const resolveHtmlTarget = (value) => {
    if (!value) return "";
    const trimmed = value.replace(/^\/+/, "");
    if (/\.html$/i.test(trimmed)) return trimmed;
    const normalized = normalizePath(trimmed);
    if (HTML_TARGETS[normalized]) return HTML_TARGETS[normalized];
    if (!normalized) return "";
    const fallback = normalized.includes("/")
      ? normalized.replace(/\//g, "_")
      : normalized;
    return `${fallback}.html`;
  };
  const toHref = (target) => {
    if (!target) return null;
    if (/^https?:/i.test(target)) return target;
    const resolved = resolveHtmlTarget(target);
    if (!resolved) return "/";
    return resolved.startsWith("/") ? resolved : `/${resolved}`;
  };

  function validRole(role){ return !!ROUTE_MAP[role]; }

  function setRole(role) {
    if (!validRole(role)) return;
    try { w.localStorage.setItem(STORAGE_KEY, role); } catch(e){}
    w.__ALERTAID_ROLE__ = role;
  }

  function getRole() {
    const url = new URL(w.location.href);
    const qRole = url.searchParams.get("role");
    if (validRole(qRole)) { setRole(qRole); return qRole; }

    if (validRole(w.__ALERTAID_ROLE__)) return w.__ALERTAID_ROLE__;
    try {
      const stored = w.localStorage.getItem(STORAGE_KEY);
      if (validRole(stored)) { w.__ALERTAID_ROLE__ = stored; return stored; }
    } catch(e){}

    const cur = fileOf();
    for (const role of Object.keys(ROUTE_MAP)) {
      for (const key of Object.keys(ROUTE_MAP[role])) {
        if (normalizePath(ROUTE_MAP[role][key]) === normalizePath(cur) &&
            (key === "dashboard" || key === "profile" || key === "settings")) {
          setRole(role);
          return role;
        }
      }
    }

    const ref = d.referrer ? fileOf(new URL(d.referrer).pathname) : "";
    if (ref) {
      for (const role of Object.keys(ROUTE_MAP)) {
        for (const key of Object.keys(ROUTE_MAP[role])) {
          if (normalizePath(ROUTE_MAP[role][key]) === normalizePath(ref)) {
            setRole(role);
            return role;
          }
        }
      }
    }
    return null;
  }

  function ensureBranding() {
    const head = d.head || d.getElementsByTagName('head')[0];
    if (head && !head.querySelector('link[data-alertaid-favicon]')) {
      const link = d.createElement('link');
      link.rel = 'icon';
      link.type = 'image/png';
      link.href = '/favicon.png';
      link.setAttribute('data-alertaid-favicon', 'true');
      head.appendChild(link);
    }
  }

  function ensureTitle() {
    if (!d.title || d.title.trim().length === 0 || d.title === w.location.href) {
      d.title = 'AlertAid';
    }
  }

  function routeFor(routeKey, role) {
    const r = role || getRole();
    if (!validRole(r)) return null;
    return ROUTE_MAP[r][routeKey] || null;
  }

  function go(routeKey, opts = {}) {
    const r = opts.role || getRole();
    const target = routeFor(routeKey, r);
    if (!target) { console.warn("No route for", routeKey, "role:", r); return; }
    const url = new URL(w.location.href);
    url.searchParams.delete("role");
    const qs = url.search ? url.search : "";
    const hash = w.location.hash || "";
    w.location.href = toHref(target) + qs + hash;
  }

  w.RoleRouter = { setRole, getRole, go, ROUTE_MAP };

  d.addEventListener("DOMContentLoaded", () => {
    const cur = fileOf();

    (function learnRoleFromPage(){
      for (const role of Object.keys(ROUTE_MAP)) {
        for (const key of ["dashboard","profile","settings"]) {
          if (normalizePath(ROUTE_MAP[role][key]) === normalizePath(cur)) {
            setRole(role);
            return;
          }
        }
      }
    })();

    ensureBranding();
    ensureTitle();

    const meta = qs('meta[name="page-key"]');
    if (meta) {
      const key = (meta.getAttribute("content")||"").trim();
      const r = getRole();
      if (key && validRole(r)) {
        const shouldBe = routeFor(key, r);
        if (shouldBe && normalizePath(shouldBe) !== normalizePath(cur)) {
          w.location.replace(toHref(shouldBe) + w.location.search + w.location.hash);
          return;
        }
      }
    }

    qsa('[data-route]').forEach(el => {
      const key = el.getAttribute("data-route");
      if (!key) return;
      const roleForLinks = getRole();
      const preferredTarget = routeFor(key, roleForLinks) || key;
      const resolvedHref = toHref(preferredTarget);
      if (resolvedHref) {
        el.setAttribute("href", resolvedHref);
      } else if (!el.hasAttribute("href")) {
        el.setAttribute("href", "javascript:void(0)");
      }
      const handler = (ev) => { ev.preventDefault(); go(key); };
      el.addEventListener("click", handler);
      if (el.tagName !== "A") {
        el.setAttribute("role", "button");
        el.tabIndex = 0;
        el.addEventListener("keydown", (e)=>{ if (e.key==="Enter"||e.key===" ") handler(e); });
      }
    });

    w.goTo = function(key){ go(key); };

  });
})(window, document);
