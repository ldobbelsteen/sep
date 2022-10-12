package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.ActionMessages;

import java.util.List;

/**
 * Record contains all needed data for an ActionMessage.
 *
 * @param messageType the message type. See {@link ActionMessages} for possible types.
 * @param data the data that needs to be placed inside the gaps of the message.
 * @author Valentijn van den Berg (1457446)
 * @since 03-04-2022
 */
public record ActionMessageDT(ActionMessages messageType, List<String> data) {
}
