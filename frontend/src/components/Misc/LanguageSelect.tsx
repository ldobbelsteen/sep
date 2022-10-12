import React from "react";
import { languageNames, supportedLanguages } from "../../utils/language";
import { LanguageContext } from "./Helpers";
import { Select } from "./Select";

/**
 * Simple selector which allows switching between languages and updating the
 * language through the language context. This will update the language of the
 * entire interface.
 */
export const LanguageSelect = () => {
  const { language, setLanguage } = React.useContext(LanguageContext);

  return (
    <Select
      multiSelect={false}
      value={language}
      options={supportedLanguages}
      onChange={(language) => language && setLanguage(language.value)}
      display={(language) => languageNames[language]}
    />
  );
};
