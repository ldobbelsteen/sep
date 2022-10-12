import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../utils/api/http";
import { User } from "../utils/api/types";
import { Game } from "./Game";
import { LoadingAnimation } from "./Misc/Loading";
import { Login } from "./Misc/Login";

/**
 * The main home interface. This handles showing either the login/home screen or
 * the main game interface depending on whether the user is logged in or not.
 */
export const Home = () => {
  const [user, setUser] = useState<User | null | undefined>(null);

  /** Get user on first load */
  useEffect(() => {
    HTTP.User.current()
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          setUser(res);
        }
        return null;
      })
      .catch((err) => {
        setUser(undefined); // an error is thrown when the user is not logged in, so set to undefined
        console.error(err); // don't show toast message like normal
      });
  }, []);

  /** Show loading animation if user is still being loaded */
  if (user === null) {
    return <LoadingAnimation />;
  }

  /**
   * Return login screen if the user has turned out to not be logged in or the
   * main game interface if they are.
   */
  return (
    <div className="full-size row-center-children">
      {!user ? (
        <Login />
      ) : (
        <Game
          user={user}
          setGameId={(id) => setUser({ ...user, gameId: id })}
        />
      )}
    </div>
  );
};
