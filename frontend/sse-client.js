// Minimal EventSource helper with automatic reconnect and callback
export function subscribeSSE(url, { onMessage, onError } = {}) {
  let es = new EventSource(url, { withCredentials: false });
  let closed = false;

  function setup() {
    es.onmessage = (e) => {
      if (typeof onMessage === 'function') onMessage(e);
    };
    es.onerror = (e) => {
      if (closed) return;
      if (typeof onError === 'function') onError(e);
      // EventSource auto-reconnects by default; no manual retry needed
    };
  }

  setup();

  return {
    close() { closed = true; try { es.close(); } catch(_){} },
  };
}
