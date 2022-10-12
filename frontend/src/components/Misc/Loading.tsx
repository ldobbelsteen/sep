import React from "react";
import loading from "../../static/loading.svg";

/**
 * Show a simple animated icon that indicates that something is loading. The
 * icon has constant size and is centered within the entire size the component is given.
 */
export const LoadingAnimation = () => {
  return (
    <div className="full-size row-center-children">
      <img width={96} height={96} src={loading} alt="Loading" />
    </div>
  );
};
