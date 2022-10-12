import React, { useEffect, useState } from "react";
import { readableDuration, readableTime } from "../../utils/tools";

/**
 * Returns the current time as a simple string in hh:mm format. It uses timeouts
 * which allow it to be efficient and update at the exact same time as the system clock.
 */
export const Time = () => {
  const [time, setTime] = useState(new Date());
  const [ready, setReady] = useState(false);

  /**
   * The component is ready when an interval is set. The interval should be
   * started exactly when a new minute starts, so first we have to wait until
   * the next minute ticks over.
   */
  useEffect(() => {
    if (!ready) {
      const now = new Date();
      const toNextSecond = 1000 - now.getMilliseconds();
      const toNextMinute = toNextSecond + 1000 * (60 - (now.getSeconds() + 1));
      const timeout = setTimeout(() => setReady(true), toNextMinute);
      return () => clearTimeout(timeout);
    } else {
      const update = () => setTime(new Date());
      const interval = setInterval(update, 1000 * 60);
      update();
      return () => clearInterval(interval);
    }
  }, [ready]);

  const readable = readableTime(time);
  return <>{readable}</>;
};

/**
 * Returns the time left until a deadline as a simple string in hh:mm:ss format.
 * It uses timeouts which allow it to be efficient and rerender only once every second.
 */
export const Countdown = (props: { deadline: Date }) => {
  const [time, setTime] = useState(new Date());
  const [ready, setReady] = useState(false);

  /**
   * The component is ready when an interval is set. The interval should be
   * started exactly when a new second starts, so first we have to wait until
   * the next second ticks over.
   */
  useEffect(() => {
    if (!ready) {
      const now = new Date();
      const toNextSecond = 1000 - now.getMilliseconds();
      const timeout = setTimeout(() => setReady(true), toNextSecond);
      return () => clearTimeout(timeout);
    } else {
      const update = () => setTime(new Date());
      const interval = setInterval(update, 1000);
      update();
      return () => clearInterval(interval);
    }
  }, [ready]);

  const timeLeft = props.deadline.getTime() - time.getTime();
  const readable = readableDuration(timeLeft / 1000);
  return <span>{readable}</span>;
};
