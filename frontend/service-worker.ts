import { clientsClaim } from "workbox-core";
import { precacheAndRoute, cleanupOutdatedCaches } from "workbox-precaching";

declare let self: ServiceWorkerGlobalScope;

/** Cleaup assets from previous versions of the app */
cleanupOutdatedCaches();

/** Cache static assets to conform to PWA standard */
precacheAndRoute(self.__WB_MANIFEST);

/** Claim currently available clients */
self.skipWaiting().catch(console.error);
clientsClaim();
