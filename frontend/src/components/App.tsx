import "../styles.scss";
import React, { useState } from "react";
import { createRoot } from "react-dom/client";
import { Toaster } from "react-hot-toast";
import { registerSW } from "virtual:pwa-register"; // eslint-disable-line import/no-unresolved
import {
  createTranslators,
  getLanguageFromStorage,
  getOptimalLanguage,
  Language,
  storeLanguageInStorage,
} from "../utils/language";
import { Home } from "./Home";
import {
  LanguageContext,
  ToastErrorAny,
  TranslatorContext,
} from "./Misc/Helpers";

/**
 * Entrypoint for the web interface. The app is a single page app meaning there
 * is no notion of pages in the URL. Everything happens inside of this main component.
 */
const App = () => {
  const [language, setLanguage] = useState(
    getLanguageFromStorage() ?? getOptimalLanguage(window.navigator.languages)
  );

  /**
   * Provide language and translator contexts allowing components to access
   * translations and change the selected language. Also renders a container for
   * toast notifications.
   */
  return (
    <>
      <Toaster
        toastOptions={{
          className: "allow-select",
          style: {
            background: "var(--theme-color-darkest)",
            color: "var(--text-color)",
          },
        }}
      />
      <LanguageContext.Provider
        value={{
          language: language,
          setLanguage: (language: Language) => {
            storeLanguageInStorage(language);
            setLanguage(language);
          },
        }}
      >
        <TranslatorContext.Provider value={createTranslators(language)}>
          <Home />
        </TranslatorContext.Provider>
      </LanguageContext.Provider>
    </>
  );
};

/**
 * Register the service worker which handles caching and notifications. This is
 * what provides PWA functionality, allowing the web application to be installed.
 */
registerSW({
  onRegistered: (registration) => {
    if (registration) {
      setInterval(() => {
        registration.update().catch(ToastErrorAny);
      }, 1000 * 60 * 60);
    }
  },
});

/** Start rendering the application */
const container = document.getElementById("root");
if (container) {
  const root = createRoot(container);
  root.render(<App />);
}
