package org.lukos.model.rolesystem;

import java.util.List;
import java.util.Map;

public record RoleActionInformation(Action action, Map<EligibleType, List<Integer>> eligible, int numberOfVotes) {
}
