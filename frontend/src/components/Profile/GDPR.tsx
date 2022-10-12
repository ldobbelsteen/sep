import React from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../utils/api/http";
import { ToastErrorAny, TranslatorContext } from "../Misc/Helpers";
import { logout } from "../Misc/Logout";

/**
 * GDPR section which allows the user to download their data or delete their
 * account. Downloads the data as a file and logs the user out when they delete
 * their account.
 */
export const GPDR = () => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <section className="column-center-children gap">
      <h4>{keyTranslator("gdpr")}</h4>
      <DownloadData />
      <DeleteAccount />
    </section>
  );
};

/**
 * Button which downloads a file containing all data stored on the user in the
 * database. Contains the download tag which will cause it to download like you
 * would expect.
 */
const DownloadData = () => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <button>
      <a href={HTTP.User.GDPR.downloadUserInfo} download>
        {keyTranslator("downloadInfo")}
      </a>
    </button>
  );
};

/**
 * Button which instructs the backend to delete the account of the user. Forces
 * a logout after a successful response. When the user is in a game while doing
 * this, their data will be removed once the game finishes.
 */
const DeleteAccount = () => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <button
      onClick={() => {
        HTTP.User.GDPR.deleteAccount()
          .then((res) => {
            if (HTTP.isError(res)) {
              toast.error(res.error);
            } else {
              if (res.message) {
                toast.success(res.message);
                logout();
              }
            }
            return null;
          })
          .catch(ToastErrorAny);
      }}
    >
      {keyTranslator("deleteAccount")}
    </button>
  );
};
