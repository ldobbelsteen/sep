import React from "react";
import { ToastErrorAny } from "./Helpers";

/**
 * Show a string of text which acts as a button. Upon clicking the button the
 * copy text will be put into the user's clipboard. If no copy string is
 * specified the text string will be copied.
 */
export const ClickToCopy = (props: { text: string; copy?: string }) => {
  return (
    <button
      style={{ all: "unset", cursor: "pointer" }}
      onClick={() => {
        navigator.clipboard
          .writeText(props.copy ?? props.text)
          .catch(ToastErrorAny);
      }}
    >
      {props.text}
    </button>
  );
};
