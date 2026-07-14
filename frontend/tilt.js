/**
 * tilt.js — cursor-driven tilt for .glass-card elements (adds real depth,
 * ~1KB, zero dependencies). Include after premium-theme.css is applied.
 *   <script src="tilt.js"></script>
 */
(function (w, d) {
  'use strict';
  if (w.matchMedia && w.matchMedia('(prefers-reduced-motion: reduce)').matches) return;
  if ('ontouchstart' in w) return; // skip on touch devices

  function attach(card) {
    const strength = 8; // max degrees
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect();
      const x = (e.clientX - rect.left) / rect.width - 0.5;
      const y = (e.clientY - rect.top) / rect.height - 0.5;
      card.style.transform = `translateY(-6px) rotateX(${(-y * strength).toFixed(2)}deg) rotateY(${(x * strength).toFixed(2)}deg)`;
    });
    card.addEventListener('mouseleave', () => { card.style.transform = ''; });
  }

  function init() {
    d.querySelectorAll('.glass-card').forEach(attach);
  }

  if (d.readyState === 'loading') d.addEventListener('DOMContentLoaded', init);
  else init();

  // re-scan if content is added dynamically (dashboards fetch cards via API)
  w.reinitTilt = init;
})(window, document);
