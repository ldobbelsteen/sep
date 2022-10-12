/** SVGs have their URL as type */
declare module "*.svg" {
  const url: string;
  export default url;
}

/** Format of the translations file */
declare module "*.json" {
  const translations: Record<string, Record<string, string>>;
  export default translations;
}
