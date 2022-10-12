package org.lukos.model.voting;

import org.lukos.database.VoteDB;
import org.lukos.model.exceptions.voting.NoSuchVoteException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class for doing functions that retrieve (specific) {@link Vote}(s).
 *
 * @author Rick van der Heijden (1461923)
 * @since 27-03-2022
 */
public class VoteRetriever {

    /** Private constructor, as it is a helper class, and it enforces static methods */
    private VoteRetriever() {
    }

    /**
     * Given the ID for a {@link Vote}, returns a suitable (non-abstract) representation of that {@link Vote}.
     *
     * @param voteId ID of the {@link Vote} that should be returned
     * @return a non-abstract {@link Vote} object suitable for representing the {@link Vote} with ID {@code voteId}
     * @throws NoSuchVoteException if the {@link Vote} with ID {@code voteId} does not exist.
     * @throws SQLException        when database-related issues occur
     */
    public static Vote retrieveVote(int voteId) throws SQLException, NoSuchVoteException {
        ResultSet resultSet = VoteDB.findVoteByID(voteId);
        if (!resultSet.next()) {
            throw new NoSuchVoteException("That vote does not exist.");
        }
        VoteType voteType = VoteType.valueOf(resultSet.getString("voteType"));
        return getVoteByType(voteId, voteType);
    }

    /**
     * Given the ID for a {@link Vote}, returns a suitable (non-abstract) representation of that {@link Vote} as long as
     * that {@link Vote} is in the {@code Instance} indicated by {@code instanceId}.
     *
     * @param voteId     ID of the {@link Vote} that should be returned
     * @param instanceId ID of the {@code Instance} in which the {@link Vote} should be
     * @return a non-abstract {@link Vote} object suitable for representing the {@link Vote} with ID {@code voteId}
     * @throws NoSuchVoteException if the {@link Vote} with ID {@code voteId} does not exist, or if it is not in the
     *                             {@code Instance} with ID {@code instanceId}
     * @throws SQLException        when database-related issues occur
     */
    public static Vote retrieveVoteByIdIfInInstance(int voteId, int instanceId)
            throws NoSuchVoteException, SQLException {
        ResultSet resultSet = VoteDB.findVoteByIdIfInInstance(voteId, instanceId);
        if (!resultSet.next()) {
            throw new NoSuchVoteException("That vote does not exist, or it is not in the given instance.");
        }
        VoteType voteType = VoteType.valueOf(resultSet.getString("voteType"));
        return getVoteByType(voteId, voteType);
    }

    /**
     * Retrieves the {@link Vote}s that are ongoing for the {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceId the ID of the {@code Instance} for which to retrieve ongoing {@link Vote}s
     * @return a {@link List} of {@link Vote}s that are ongoing in the {@code Instance}
     * @throws SQLException        when an exception occurs in a database operation
     * @throws NoSuchVoteException if the {@link VoteType} does not exist
     */
    public static List<Vote> retrieveOngoingVotesByInstance(int instanceId) throws SQLException, NoSuchVoteException {
        ResultSet resultSet = VoteDB.retrieveOngoingVotesByInstance(instanceId);
        return getVotesFromResultSet(resultSet);
    }

    /**
     * Returns a {@link List} of {@link Vote}s that can be retrieved from the {@code resultSet}.
     *
     * @param resultSet The {@link ResultSet} from which to retrieve the votes.
     * @return a {@link List} of {@link Vote}s that are retrieved from the {@code resultSet}
     * @throws SQLException        when an exception occurs in a database operation
     * @throws NoSuchVoteException if the {@link VoteType} does not exist
     */
    private static List<Vote> getVotesFromResultSet(ResultSet resultSet) throws SQLException, NoSuchVoteException {
        List<Vote> votes = new ArrayList<>();

        while (resultSet.next()) {
            int vid = resultSet.getInt("voteID");
            VoteType voteType = VoteType.valueOf(resultSet.getString("voteType"));
            votes.add(getVoteByType(vid, voteType));
        }
        return votes;
    }

    /**
     * Given the ID and type of {@link Vote}, returns an object that is a suitable representation of that {@link Vote}.
     *
     * @param vid      the ID of the {@link Vote}
     * @param voteType the type of the {@link Vote}, as defined in the {@link VoteType} {@code enum}
     * @return a {@link Vote} according to its {@link VoteType}
     * @throws NoSuchVoteException if the {@link VoteType} does not exist
     */
    private static Vote getVoteByType(int vid, VoteType voteType) throws NoSuchVoteException {
        switch (voteType) {
            case MAYOR, LYNCH -> {
                return new PlayerVote(vid);
            }
            case ALPHA_WOLF -> {
                return new AlphaWolfVote(vid);
            }
            default -> throw new NoSuchVoteException("There does not exist a vote with such a type.");
        }
    }
}
