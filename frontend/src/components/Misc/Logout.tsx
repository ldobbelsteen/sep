import React from "react";
import HTTP from "../../utils/api/http";
import { ToastErrorAny, TranslatorContext } from "./Helpers";

/**
 * Logout a the currently logged in user by sending a POST request to the logout
 * endpoint and reloading after regardless of the response.
 */
export const logout = () => {
  HTTP.logout()
    .finally(() => window.location.reload())
    .catch(ToastErrorAny);
};

/** Button which logs out the user. */
export const Logout = () => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return <button onClick={logout}>{keyTranslator("logOut")}</button>;
};
