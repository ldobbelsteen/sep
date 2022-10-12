import React from "react";

/**
 * Show a basic button which will redirect to a specific URL once clicked on.
 * Redirects to in the current tab.
 */
export const LinkButton = (props: { url: string; text: string }) => {
  return (
    <a href={props.url}>
      <button>{props.text}</button>
    </a>
  );
};
