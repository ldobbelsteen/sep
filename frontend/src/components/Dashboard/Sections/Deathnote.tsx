import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../../utils/api/http";
import { GameInfo, Player } from "../../../utils/api/types";
import { ToastErrorAny, TranslatorContext } from "../../Misc/Helpers";

/**
 * Interface to let the user view their current deathnote and update it by
 * submitting a new one.
 */
export const Deathnote = (props: { user: Player; game: GameInfo }) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [input, setInput] = useState("");

  /** Get the user's current deathnote */
  useEffect(() => {
    HTTP.Game.Player.obituary(props.game.id, props.user.entry.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          if (res.deathNote) {
            setInput(res.deathNote);
          }
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [props.game.id, props.user.entry.id]);

  /** Handle submitting a new deathnote */
  const submit = () => {
    if (input) {
      HTTP.Game.Player.submitDeathnote(props.game.id, input)
        .then((res) => {
          if (HTTP.isError(res)) {
            toast.error(res.error);
          } else {
            toast.success(keyTranslator("successfullySubmitted"));
          }
          return null;
        })
        .catch(ToastErrorAny);
    }
  };

  /**
   * Render the deatnote input area as a textarea, which allows creating
   * multiline deathnotes by pressing enter. If the deathnote becomes too large,
   * it will be scrollable.
   */
  return (
    <section className="column-center-children gap">
      <h2>{keyTranslator("deathnote")}</h2>
      <div className="full-size" style={{ display: "flex" }}>
        <textarea
          value={input}
          onChange={(ev) => setInput(ev.target.value)}
          placeholder={keyTranslator("typeMessage")}
          style={{ flexGrow: 1 }}
        />
        <button onClick={submit}>{keyTranslator("send")}</button>
      </div>
    </section>
  );
};
