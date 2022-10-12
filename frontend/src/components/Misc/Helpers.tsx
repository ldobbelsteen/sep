import React from "react";
import { toast } from "react-hot-toast";
import {
  createTranslators,
  Language,
  supportedLanguages,
} from "../../utils/language";

/**
 * Throw toast error for unknown error type. Should only be used with unknown
 * error types in situations where the error would be unexpected.
 */
export const ToastErrorAny = (err: unknown) => {
  toast.error("Unexpected error: " + JSON.stringify(err));
};

/** Context for sharing a translator of the current language among components */
export const TranslatorContext = React.createContext(
  createTranslators(supportedLanguages[0])
);

/** Context for sharing the current language among components */
export const LanguageContext = React.createContext({
  language: supportedLanguages[0] as Language,
  setLanguage: (language: Language) => {
    void language;
  },
});
