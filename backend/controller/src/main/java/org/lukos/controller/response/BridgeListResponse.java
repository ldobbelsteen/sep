package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype that gives information about bridges.
 *
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
public class BridgeListResponse extends SuccessResponse {
    /** The list with bridges the response gives information about. */
    private final List<BridgeEntry> bridgeNames;

    /**
     * Constructor for responses of {@code BridgeListResponse}.
     *
     * @param bridgeNames the list with bridges
     */
    public BridgeListResponse(List<BridgeEntry> bridgeNames) {
        super(null);
        this.bridgeNames = bridgeNames;
    }

    public List<BridgeEntry> getBridgeNames() {
        return bridgeNames;
    }
}
