import fieldTranslations from "../static/field-translations.json";
import keyTranslations from "../static/key-translations.json";
import { capitalizeFirstLetter, substituteFields } from "./tools";

/** The languages supported by the app */
export const supportedLanguages = ["en", "nl"] as const;
export type Language = typeof supportedLanguages[number];
export const languageNames: Record<Language, string> = {
  en: "English",
  nl: "Nederlands",
};

/** Store a language into LocalStorage for persistence accros page loads */
export const storeLanguageInStorage = (language: Language) => {
  window.localStorage.setItem("language", language);
};

/** Fetch the language stored into LocalStorage if it exists and is valid */
export const getLanguageFromStorage = (): Language | undefined => {
  const language = window.localStorage.getItem("language");
  if (!language) return;

  for (const supportedLanguage of supportedLanguages) {
    if (language === supportedLanguage) {
      return supportedLanguage;
    }
  }
};

/**
 * Get the optimal available language based on an array of languages. The array
 * of languages is assumed to be the browser's languages. Goes through the
 * browser's preferred languages and checks if any are supported. If none are,
 * the first supported language is returned.
 */
export const getOptimalLanguage = (
  browserLanguages: readonly string[]
): Language => {
  for (const browserLanguage of browserLanguages) {
    const preferredLanguage = browserLanguage.substring(0, 2);
    for (const supportedLanguage of supportedLanguages) {
      if (preferredLanguage === supportedLanguage) {
        return preferredLanguage;
      }
    }
  }

  return supportedLanguages[0];
};

/**
 * Factory function for creating an object containing two functions. One of them
 * can retrieve translations corresponding with a key from the translations
 * file. The other can get translations of sentences with special fields being
 * substituted by abitrary data.
 */
export const createTranslators = (
  language: Language
): {
  keyTranslator: (key: string, capitalize?: boolean) => string;
  fieldTranslator: (key: string, fields: string[]) => string;
} => {
  const keyTranslator = (key: string, capitalize = true) => {
    const values = keyTranslations[key];
    if (!values) return "unknown-" + key;
    const translation = values[language];
    if (!translation) return "missing-" + key;

    if (capitalize) {
      return capitalizeFirstLetter(translation);
    } else {
      return translation;
    }
  };

  const fieldTranslator = (key: string, fields: string[]) => {
    const values = fieldTranslations[key];
    if (!values) return "unknown-" + key;
    const translation = values[language];
    if (!translation) return "missing-" + key;

    return substituteFields(translation, "🅳", "🆃", keyTranslator, fields);
  };

  return { keyTranslator, fieldTranslator };
};
