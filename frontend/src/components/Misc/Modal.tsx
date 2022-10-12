import React from "react";

/**
 * Generic modal component which fills up the entire screen with semitransparent
 * black and a page in the middle of the screen. The supplied children will be
 * rendered inside of this page. The horizontal width is constant, and the
 * vertical width is limited by the viewport height. If the content is too large
 * vertically, a scrollbar will appear.
 */
export const Modal = (props: {
  children: React.ReactNode;
  close: () => void;
}) => {
  return (
    <div
      className="modal row-center-children"
      onClick={props.close}
      onKeyDown={props.close}
      aria-hidden
    >
      <section
        className="content column-center-children"
        onClick={(e) => e.stopPropagation()}
        onKeyDown={(e) => e.stopPropagation()}
        aria-hidden
      >
        {props.children}
      </section>
    </div>
  );
};
