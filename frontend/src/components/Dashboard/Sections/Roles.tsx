import React from "react";
import Schema from "../../../utils/api/schema";
import { CollapsibleList } from "../../Misc/CollapsibleList";
import { TranslatorContext } from "../../Misc/Helpers";

/**
 * Show an overview of all roles in the game. A list of role names, each of
 * which can be expanded to show an explanation of the corresponding role. The
 * list becomes scrollable if too large.
 */
export const Roles = () => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <section className="column-center-children gap">
      <h2>{keyTranslator("roleOverview")}</h2>
      <CollapsibleList
        items={Schema.RoleList.map((role) => ({
          buttonText: keyTranslator(role),
          collapsibleText: keyTranslator(role + "Explanation"),
        }))}
      />
    </section>
  );
};
