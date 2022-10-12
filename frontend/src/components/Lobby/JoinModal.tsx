import React, { useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../utils/api/http";
import { ToastErrorAny, TranslatorContext } from "../Misc/Helpers";
import { LoadingAnimation } from "../Misc/Loading";
import { Modal } from "../Misc/Modal";

/**
 * Interface where a lobby can be joined using a user-supplied invite code. The
 * code should be shared by the person who created the lobby and filled in this
 * component.
 */
export const JoinModal = (props: {
  close: () => void;
  success: (gameId: number) => void;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [input, setInput] = useState("");
  const [submitting, setSubmitting] = useState(false);

  /**
   * Submit the invite code and submit the game ID of the newly joined game back
   * up to the parent such that a lobby can be rendered.
   */
  const handleJoin = () => {
    if (input) {
      setSubmitting(true);
      HTTP.Lobby.join({ joinCode: parseInt(input) })
        .then((res) => {
          if (HTTP.isError(res)) {
            toast.error(res.error);
          } else {
            props.success(res.gameId);
          }
          return null;
        })
        .finally(() => setSubmitting(false))
        .catch(ToastErrorAny);
    } else {
      toast.error(keyTranslator("emptyInput"));
    }
  };

  /** Render the input field and submit and back buttons */
  return (
    <Modal close={props.close}>
      {submitting ? (
        /** Show loading animation while submitting */
        <LoadingAnimation />
      ) : (
        <>
          <h2>{keyTranslator("joinGame")}</h2>
          <input
            type="number"
            value={input}
            style={{ textAlign: "center" }}
            onChange={(ev) => setInput(ev.target.value)}
            placeholder={keyTranslator("code")}
          />
          <div className="row-center-children">
            <button onClick={props.close}>{keyTranslator("back")}</button>
            <button onClick={handleJoin}>{keyTranslator("join")}</button>
          </div>
        </>
      )}
    </Modal>
  );
};
