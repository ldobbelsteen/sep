import keyTranslations from "../static/key-translations.json";
import {
  createTranslators,
  getLanguageFromStorage,
  getOptimalLanguage,
  storeLanguageInStorage,
  supportedLanguages,
} from "./language";

test("testNonExistentLanguagePreference", () => {
  expect(getLanguageFromStorage()).toBe(undefined);
});

test("testLanguagePersistence", () => {
  storeLanguageInStorage("en");
  expect(getLanguageFromStorage()).toBe("en");
});

test("testOptimalLanguageDetection", () => {
  expect(getOptimalLanguage(["nl", "de", "en"])).toBe("nl");
  expect(getOptimalLanguage(["de", "nl", "en"])).toBe("nl");
  expect(getOptimalLanguage(["de", "en", "nl"])).toBe("en");
  expect(getOptimalLanguage(["de", "zh"])).toBe("en");
});

test("testKeyTranslations", () => {
  /** Test consistency for a handful of keys for all supported languages */
  supportedLanguages.forEach((tag) => {
    const { keyTranslator } = createTranslators(tag);
    expect(keyTranslator("werewolf", false)).toBe(
      keyTranslations["werewolf"][tag]
    );
    expect(keyTranslator("day", false)).toBe(keyTranslations["day"][tag]);
    expect(keyTranslator("house", false)).toBe(keyTranslations["house"][tag]);
  });

  /** Test capitalization and missing keys/translations */
  const { keyTranslator } = createTranslators("en");
  expect(keyTranslator("werewolf")).toBe("Werewolf");
  expect(keyTranslator("werewolf", true)).toBe("Werewolf");
  expect(keyTranslator("werewolf", false)).toBe("werewolf");
  expect(keyTranslator("gameName")).toBe("Game name");
  expect(keyTranslator("gameName", true)).toBe("Game name");
  expect(keyTranslator("gameName", false)).toBe("game name");
  expect(keyTranslator("nonExistentKey")).toBe("unknown-nonExistentKey");
  expect(keyTranslator("missingTranslation")).toBe(
    "missing-missingTranslation"
  );
});

test("testFieldTranslations", () => {
  const { fieldTranslator } = createTranslators("en");
  expect(fieldTranslator("nightKillBroadcastMessage", ["Jeff"])).toBe(
    "Jeff has died tonight."
  );
  expect(fieldTranslator("nonExistentKey", [])).toBe("unknown-nonExistentKey");
  expect(fieldTranslator("missingTranslation", [])).toBe(
    "missing-missingTranslation"
  );
  expect(fieldTranslator("nightKillBroadcastMessage", [])).toBe(
    "not-enough-fields"
  );
  expect(fieldTranslator("youHaveBeenRevivedMessage", ["Jeff"])).toBe(
    "no-fields-expected"
  );
  expect(
    fieldTranslator("seeRoleMessage", ["Jeff", "PRIVATE_INVESTIGATOR"])
  ).toBe("The main role of Jeff is Private investigator.");
});
