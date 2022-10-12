import React, { useState } from "react";
import expand from "../../static/expand.svg";
import { TranslatorContext } from "./Helpers";

/**
 * Show a list of collapsible items. Each item is a string of text and an icon
 * indicating collapsibility next to it. Clicking on an item will expand it and
 * show more text. Only one item can be expanded at the same time. If the list
 * is too large, the height will be limited and the list will be scrollable.
 */
export const CollapsibleList = (props: {
  items: { buttonText: string; collapsibleText: string }[];
}) => {
  const [selected, setSelected] = useState(-1);

  return (
    <div className="subsection scrollbox">
      <div className="column-center-children">
        {props.items.map((item, index) => (
          <>
            <CollapseButton
              index={index}
              text={item.buttonText}
              selected={selected}
              setSelected={setSelected}
            />
            <CollapseText
              index={index}
              text={item.collapsibleText}
              selected={selected}
              setSelected={setSelected}
            />
          </>
        ))}
      </div>
    </div>
  );
};

/**
 * Render a button which will set the selected item to this button's index when
 * the user clicks on it.
 */
const CollapseButton = (props: {
  text: string;
  index: number;
  selected: number;
  setSelected: (s: number) => void;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <button
      className="tiny-margin"
      style={{ width: "99%" }}
      onClick={() => {
        if (props.selected === props.index) {
          props.setSelected(-1);
        } else {
          props.setSelected(props.index);
        }
      }}
    >
      {props.text}
      <img
        src={expand}
        className="tiny-size"
        style={
          props.selected === props.index ? { transform: "rotate(180deg)" } : {}
        }
        alt={keyTranslator("expand")}
      />
    </button>
  );
};

/**
 * Render a divisor which contains the collapsible text when the corresponding
 * button is pressed/selected.
 */
const CollapseText = (props: {
  text: string;
  index: number;
  selected: number;
  setSelected: (s: number) => void;
}) => (
  <span
    className="hyphen-breaks tiny-margin max-width"
    style={props.selected !== props.index ? { display: "none" } : {}}
  >
    {props.text}
  </span>
);
