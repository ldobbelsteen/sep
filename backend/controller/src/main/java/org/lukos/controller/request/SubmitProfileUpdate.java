package org.lukos.controller.request;

/**
 * The request datatype for joining a game.
 *
 * @author Marco Pleket (1295713)
 * @since 28-03-2022
 */
public class SubmitProfileUpdate {
    /** The enum value for the key of the dataslot to be changed */
    private ProfileData dataKey;

    /** The data the dataslot will be updated with */
    private String data;

    /**
     * Return the key of the data slot
     *
     * @return the key of the data slot
     */
    public ProfileData getDataKey() {
        return dataKey;
    }

    public void setDataKey(ProfileData dataKey) {
        this.dataKey = dataKey;
    }

    /**
     * Return the data the slot is updated with
     *
     * @return the data of which the slot is updated with
     */
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
