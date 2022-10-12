import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../../utils/api/http";
import { ActionMessage, GameInfo } from "../../../utils/api/types";
import { ToastErrorAny, TranslatorContext } from "../../Misc/Helpers";

/**
 * Section which renders the historical action result messages. Uses the field
 * translator to substitute message fields into translated sentences to maintain
 * a fully translated interface.
 */
export const Messages = (props: { game: GameInfo }) => {
  const { keyTranslator, fieldTranslator } =
    React.useContext(TranslatorContext);

  const [messages, setMessages] = useState<ActionMessage[] | null>(null);

  /** Fetch action messages on first load and phase change */
  useEffect(() => {
    HTTP.Game.Purpose.messages(props.game.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          setMessages(res.actions);
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [props.game.id, props.game.phase]);

  /** Don't render if there are no messages anyways */
  if (!messages || messages.length === 0) return null;

  /**
   * Render as scrollbox such that the component won't get too large vertically
   * and a scrollbar appears instead.
   */
  return (
    <section className="column-center-children gap">
      <h2>{keyTranslator("messages")}</h2>
      <div className="subsection scrollbox">
        <div className="column-center-children">
          {messages &&
            messages.map((message, index) => (
              <span key={index}>
                {fieldTranslator(message.messageType, message.data)}
              </span>
            ))}
        </div>
      </div>
    </section>
  );
};
