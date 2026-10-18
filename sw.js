// TOP3 FINANCES — service worker
// Keeps the app installable on Android and gives a basic offline fallback.
// Deliberately network-first: this is live financial data, so we always
// want the freshest copy when online, and only fall back to the cached
// shell when there's no connection at all.

const CACHE_NAME = 'top3finances-shell-v1';

self.addEventListener('install', (event) => {
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(
        keys
          .filter((key) => key !== CACHE_NAME)
          .map((key) => caches.delete(key))
      )
    )
  );
  self.clients.claim();
});

self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') return;

  const url = new URL(event.request.url);

  // Never intercept the sync endpoint — always hit the network directly
  // so loan data stays live and in sync across devices.
  if (url.hostname.includes('npoint.io')) return;

  event.respondWith(
    fetch(event.request)
      .then((response) => {
        const copy = response.clone();
        caches.open(CACHE_NAME).then((cache) => cache.put(event.request, copy));
        return response;
      })
      .catch(() => caches.match(event.request))
  );
});
