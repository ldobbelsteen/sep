import {
  capitalizeFirstLetter,
  normalizeCapitalization,
  readableDuration,
  readableTime,
  removeUpUntilLast,
  toCamelCase,
  toSnakeCase,
} from "./tools";

test("testTimestampToReadable", () => {
  expect(readableTime(new Date("Fri Apr 01 2022 09:56:55 GMT+0200"))).toBe(
    "07:56"
  );
  expect(readableTime(new Date("Wed Mar 23 2022 02:23:21 GMT+0100"))).toBe(
    "01:23"
  );
  expect(readableTime(new Date("Tue Feb 15 2022 23:01:01 GMT+0600"))).toBe(
    "17:01"
  );
});

test("testDurationToReadable", () => {
  expect(readableDuration(1432)).toBe("00:23:52");
  expect(readableDuration(14329)).toBe("03:58:49");
  expect(readableDuration(0)).toBe("00:00:00");
  expect(readableDuration(-100)).toBe("00:00:00");
  expect(readableDuration(-1000)).toBe("00:00:00");
  expect(readableDuration(-10000)).toBe("00:00:00");
});

test("testRemoveUpUntilLast", () => {
  expect(removeUpUntilLast("4,5,6,7,8,3,123", ",")).toBe("123");
  expect(removeUpUntilLast("this.is.a.test", ".")).toBe("test");
});

test("testCapitalizationNormalization", () => {
  expect(normalizeCapitalization("ThiSIsATEst")).toBe("Thisisatest");
  expect(normalizeCapitalization("WereWolf")).toBe("Werewolf");
});

test("testFirstLetterCapitalization", () => {
  expect(capitalizeFirstLetter("werewolf")).toBe("Werewolf");
  expect(capitalizeFirstLetter("WEREWOLF")).toBe("WEREWOLF");
  expect(capitalizeFirstLetter("wEREWOLF")).toBe("WEREWOLF");
});

test("testCamelCaseConversion", () => {
  const tests: Record<string, string> = {
    "THIS_IS_A_TEST": "thisIsATest",
    "this_is_a_test": "thisIsATest",
    "This_Is_A_Test": "thisIsATest",
    "THIS-IS-A-TEST": "thisIsATest",
    "this-is-a-test": "thisIsATest",
    "This-Is-A-Test": "thisIsATest",
    "THIS IS A TEST": "thisIsATest",
    "this is a test": "thisIsATest",
    "This Is A Test": "thisIsATest",
    "THISISATEST": "thisisatest",
    "thisisatest": "thisisatest",
    "ThisIsATest": "thisIsAtest",
    "thisIsATest": "thisIsAtest",
    "TEST": "test",
    "test": "test",
    "Test": "test",
    "tEst": "tEst",
    "teSt": "teSt",
    "tesT": "tesT",
    "": ""
  }; // prettier-ignore
  for (const [input, output] of Object.entries(tests)) {
    expect(toCamelCase(input)).toBe(output);
  }
});

test("testSnakeCaseConversion", () => {
  expect(toSnakeCase("alphaWolf")).toBe("ALPHA_WOLF");
  expect(toSnakeCase("a")).toBe("A");
  expect(toSnakeCase("aA")).toBe("A_A");
  expect(toSnakeCase("thisIsATest")).toBe("THIS_IS_A_TEST");
});
