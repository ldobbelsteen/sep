import { Config } from "@jest/types";

const config: Config.InitialOptions = {
  preset: "ts-jest",
  testEnvironment: "jsdom",
  reporters: ["default", "jest-junit"],
  coverageReporters: ["text", "html", "cobertura"],
  coveragePathIgnorePatterns: ["src/utils/api/*"],
  collectCoverageFrom: ["src/**/*.ts"],
};

export default config;
