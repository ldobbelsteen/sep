import React from "react";
import logoDark from "../../static/logo-dark.svg";
import { Profile } from "../Profile/Profile";
import { TranslatorContext } from "./Helpers";
import { LinkButton } from "./LinkButton";

/**
 * Shows simple screen where the user can login with the backend. The login
 * button(s) redirect the user to an endpoint which will force redirects to the
 * corresponding external login pages.
 */
export const Login = () => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <div className="full-size column-center-children">
      <img
        src={logoDark}
        width={192}
        height={192}
        alt={keyTranslator("logo")}
      />
      <h1>{keyTranslator("lukos")}</h1>
      <LinkButton
        url="/oauth2/authorization/google"
        text={keyTranslator("signInWithGoogle")}
      />
      <Profile loggedIn={false} />
    </div>
  );
};
