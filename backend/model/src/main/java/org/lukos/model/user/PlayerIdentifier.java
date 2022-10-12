package org.lukos.model.user;


public record PlayerIdentifier(int instanceID, int userID) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerIdentifier playerIdentifier) {
            return instanceID == (playerIdentifier.instanceID()) && userID == (playerIdentifier.userID());
        }
        return false;
    }
}
