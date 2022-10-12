import React, { useCallback, useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../../utils/api/http";
import {
  Ballots,
  Player,
  PlayerRole,
  GameInfo,
  RunningVotes,
  Eligible,
  SimplePlayerEntry,
} from "../../../utils/api/types";
import { ToastErrorAny, TranslatorContext } from "../../Misc/Helpers";
import { Select } from "../../Misc/Select";

/**
 * Voting screen which shows a box for each of the currently running votes in
 * the game. Allows selecting players eligible for the vote and submitting the
 * vote. Shows that the player has already voted once submitted.
 */
export const Votes = (props: {
  user: Player;
  userRoles: PlayerRole[];
  game: GameInfo;
  players: Player[];
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [votes, setVotes] = useState<RunningVotes>();
  const [eligible, setEligible] = useState<Record<string, Eligible>>({});
  const [ballots, setBallots] = useState<Record<string, Ballots>>({});

  /** Update eligible players for given votes */
  const fetchEligible = useCallback(
    (vs: RunningVotes) => {
      vs.voteEntries.forEach((entry) => {
        HTTP.Game.Vote.eligible(props.game.id, entry.id)
          .then((res) => {
            if (HTTP.isError(res)) {
              toast.error(res.error);
            } else {
              setEligible((el) => {
                return { ...el, [entry.id]: res };
              });
            }
            return null;
          })
          .catch(ToastErrorAny);
      });
    },
    [props.game.id]
  );

  /** Update handed in ballots for given votes */
  const fetchBallots = useCallback(
    (vs: RunningVotes) => {
      vs.voteEntries.forEach((entry) => {
        HTTP.Game.Vote.ballots(props.game.id, entry.id)
          .then((res) => {
            if (HTTP.isError(res)) {
              toast.error(res.error);
            } else {
              setBallots((bs) => {
                return { ...bs, [entry.id]: res };
              });
            }
            return null;
          })
          .catch(ToastErrorAny);
      });
    },
    [props.game.id]
  );

  /** Get votes on first load, phase change and player list change */
  useEffect(() => {
    HTTP.Game.Vote.list(props.game.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          setVotes(res);
          fetchBallots(res);
          fetchEligible(res);
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [
    props.game.id,
    props.game.phase,
    props.players,
    fetchEligible,
    fetchBallots,
  ]);

  /** Don't render if there are no votes anyways */
  if (!votes || votes.voteEntries.length === 0) return null;

  return (
    <section className="column-center-children gap">
      <h2>{keyTranslator("voting")}</h2>
      {votes.voteEntries.map((vote, index) => {
        const options = vote.id in eligible ? eligible[vote.id].eligible : [];
        if (options.length === 0) {
          return null;
        }
        const voteBallots: Ballots =
          vote.id in ballots
            ? ballots[vote.id]
            : { message: null, ballotEntries: [] };

        return (
          <Vote
            key={index}
            title={
              keyTranslator(vote.voteType) + " " + keyTranslator("vote", false)
            }
            options={options}
            submit={(target) => {
              HTTP.Game.Vote.submit(props.game.id, vote.id, target.id).catch(
                ToastErrorAny
              );
            }}
            ballots={voteBallots}
            user={props.user}
          />
        );
      })}
    </section>
  );
};

/**
 * A subsection which represents a single running vote. Allows selecting a
 * target to submit. Shows the submitted player as selected.
 */
const Vote = (props: {
  title: string;
  options: SimplePlayerEntry[];
  ballots: Ballots;
  submit: (target: SimplePlayerEntry) => void;
  user: Player;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [selected, setSelected] = useState<SimplePlayerEntry | null>(null);
  const [submitted, setSubmitted] = useState<SimplePlayerEntry | null>(null);

  /**
   * Extract whether the player has already submitted from the ballots that have
   * already been cast. Set the currently submitted player as a result, such
   * that the selector element will have this value as selected.
   */
  useEffect(() => {
    const ballot = props.ballots.ballotEntries.find(
      (ballot) => ballot.player === props.user.entry.id
    );
    if (ballot) {
      const target = props.options.find(
        (option) => option.id === ballot.target
      );
      if (target) {
        setSubmitted(target);
      }
    }
  }, [props.ballots, props.options, props.user.entry.id]);

  /**
   * Submit the currently selected player as vote. Also pass the submitted
   * player to parent to notify of submission.
   */
  const submit = () => {
    if (selected) {
      setSubmitted(selected);
      props.submit(selected);
    }
  };

  /**
   * Render using the select wrapper and show if the player has already voted.
   * The selector will have the submitted value selected if it is defined. Add
   * 'you' tag to an option if it is the current user.
   */
  return (
    <div className="subsection">
      <div className="column-center-children">
        <span>
          <strong>{props.title}</strong>
        </span>
        {submitted && <span>{keyTranslator("alreadyVoted")}</span>}
        <div className="row-center-children">
          <Select
            multiSelect={false}
            value={(submitted || selected) ?? undefined}
            options={props.options}
            onChange={(option) => setSelected(option?.value ?? null)}
            display={(option) =>
              option.name +
              (props.user.entry.id === option.id
                ? " (" + keyTranslator("you", false) + ")"
                : "")
            }
            disabled={submitted !== null}
          />
          <button disabled={submitted !== null} onClick={submit}>
            {keyTranslator("send")}
          </button>
        </div>
      </div>
    </div>
  );
};
