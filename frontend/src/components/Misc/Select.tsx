import React from "react";
import { default as ReactSelect } from "react-select";

/**
 * A generic value with a corresponding label which represents the value in some
 * way or another.
 */
export type WithLabel<T> = {
  label: string;
  value: T;
};

/**
 * Custom HTML select element which handles data changes more ergonomically than
 * the default select element. Wraps an external library to give a more
 * ergonomic way to set styles and handle types. Allows enabling selecting
 * multiple options at once. In that case the types will change such that arrays
 * are returned instead of single values.
 */
export const Select = <T, U extends boolean>(props: {
  multiSelect: U;
  value?: T;
  options: readonly T[];
  onChange: (
    option: U extends true ? readonly WithLabel<T>[] : WithLabel<T> | null
  ) => void;
  display: (value: T) => string;
  allOptionsDisabled?: boolean;
  disabled?: boolean;
}) => {
  /** Map an option to an option suitable for the custom selector */
  const optionMapper = (option: T) => ({
    label: props.display(option),
    value: option,
  });

  /** Map all options from props to the suitable format */
  const options: WithLabel<T>[] = props.options.map(optionMapper);

  return (
    <ReactSelect
      isMulti={props.multiSelect}
      value={props.value && optionMapper(props.value)}
      options={options}
      onChange={props.onChange}
      isOptionDisabled={() => props.allOptionsDisabled ?? false}
      isDisabled={props.disabled ?? false}
      className="select-container"
      classNamePrefix="select"
      isSearchable={false}
      menuPlacement="auto"
    />
  );
};
