/** Convert a Date object to a readable time string in hh:mm format */
export const readableTime = (date: Date): string => {
  const pad = (n: number) => n.toString().padStart(2, "0");

  const minutes = date.getMinutes();
  const hours = date.getHours();

  return pad(hours) + ":" + pad(minutes);
};

/** Convert a number of seconds into a duration in hh:mm:ss */
export const readableDuration = (duration: number): string => {
  const pad = (n: number) => n.toString().padStart(2, "0");

  const secondsRaw = Math.floor(duration % 60);
  const minutesRaw = Math.floor((duration / 60) % 60);
  const hoursRaw = Math.floor((duration / 3600) % 24);

  const seconds = secondsRaw < 0 ? 0 : secondsRaw;
  const minutes = minutesRaw < 0 ? 0 : minutesRaw;
  const hours = minutesRaw < 0 ? 0 : hoursRaw;

  return pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
};

/**
 * Remove all characters from a string that precede the last occurrence of a
 * specified character. Also removes that last occurrence.
 */
export const removeUpUntilLast = (str: string, char: string): string => {
  return str.slice(str.lastIndexOf(char) + 1);
};

/** Make all letters in a string lowercase and the first letter uppercase */
export const normalizeCapitalization = (str: string): string => {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

/** Capitalize the first letter of a string and leave the rest intact */
export const capitalizeFirstLetter = (str: string): string => {
  return str.charAt(0).toUpperCase() + str.slice(1);
};

/**
 * Substitute specific characters in a string with replacement fields. Replaces
 * the characters in the same order as the specified fields. The 'direct'
 * subtitution character subtitutes the fields directly, and the 'translate'
 * subtitution character first runs the field through a translator. If there are
 * more replacements than characters, the remaining fields are concatenated with commas.
 */
export const substituteFields = (
  str: string,
  directSubChar: string,
  translateSubChar: string,
  translator: (str: string) => string,
  fields: string[]
): string => {
  /** Count the number of substitution characters and field strings */
  const directSubCharCount = str.split(directSubChar).length - 1;
  const translateSubCharCount = str.split(translateSubChar).length - 1;
  const totalSubCharCount = directSubCharCount + translateSubCharCount;
  const fieldCount = fields.length;

  /** If there are no substitution characters, but there are fields, return special string */
  if (totalSubCharCount === 0 && fieldCount > 0) {
    return "no-fields-expected";
  }

  /**
   * If there are not enough fields to replace the substitution characters,
   * return special string.
   */
  if (totalSubCharCount > fieldCount) {
    return "not-enough-fields";
  }

  /** Fill in the substitution characters until all of them have been filled. */
  for (let i = 0; i < fieldCount; i++) {
    const firstDirect = str.indexOf(directSubChar);
    const firstTranslate = str.indexOf(translateSubChar);
    const nextIsDirect =
      firstDirect !== -1 &&
      (firstDirect < firstTranslate || firstTranslate === -1);

    const nextChar = nextIsDirect ? directSubChar : translateSubChar;
    const fieldMapper: (str: string) => string = !nextIsDirect
      ? (s) => translator(toCamelCase(s))
      : (s) => s;

    /**
     * If there are no spots left after this index, concatentate the remaining
     * fields and break the loop.
     */
    if (i === totalSubCharCount - 1) {
      str = str.replace(nextChar, fields.slice(i).map(fieldMapper).join(", "));
      break;
    } else {
      str = str.replace(nextChar, fieldMapper(fields[i]));
    }
  }

  return str;
};

/** Convert snake case, kebab case, pascal case, camel case or sentences to camel case */
export const toCamelCase = (str: string): string => {
  return (
    str
      .match(/[A-Z]+[^A-Z]*|[^A-Z]+/g) // split by capital letters preceded by non-capital letters
      ?.join(" ")
      .split("-")
      .join(" ")
      .split("_")
      .join(" ")
      .split(" ")
      .map((word, index) =>
        index > 0 ? normalizeCapitalization(word) : word.toLowerCase()
      )
      .join("") || ""
  );
};

/** Convert camel case to snake case */
export const toSnakeCase = (str: string): string => {
  return str
    .split("")
    .map((c) => {
      const upper = c.toUpperCase();
      if (c === upper) {
        return "_" + upper;
      } else {
        return upper;
      }
    })
    .join("");
};
