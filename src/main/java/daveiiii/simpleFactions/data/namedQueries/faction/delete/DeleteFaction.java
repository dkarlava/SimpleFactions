package daveiiii.simpleFactions.data.namedQueries.faction.delete;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteFaction extends BaseQuery {
    public static String run (Connection connection, String factionId) throws SQLException {
        try {
            connection.setAutoCommit(false);

            // Deleting all members of the faction
            String deleteFactionMemberQuery = "DELETE FROM faction_member WHERE faction_id = ? RETURNING id";
            try (PreparedStatement stmt = connection.prepareStatement(deleteFactionMemberQuery)) {
                stmt.setString(1, factionId);
                stmt.executeQuery();
            }

            // Deleting faction disband record
            String deleteFactionDisbandQuery = """
                DELETE FROM faction_disband
               WHERE faction_id = ?
               RETURNING id;
            """;
            try (PreparedStatement stmt = connection.prepareStatement(deleteFactionDisbandQuery)) {
                stmt.setString(1, factionId);
                stmt.executeQuery();
            }

            // Deleting faction land
            String deleteFactionLandQuery = """
                DELETE FROM faction_chunk
                WHERE faction_id = ?
                RETURNING id;
            """;
            try (PreparedStatement stmt = connection.prepareStatement(deleteFactionLandQuery)) {
                stmt.setString(1, factionId);
                stmt.executeQuery();
            }

            // Deleting faction invites
            String deleteFactionInvitesQuery = """
                DELETE FROM faction_invite
                WHERE faction_id = ?
                RETURNING id;
            """;
            try (PreparedStatement stmt = connection.prepareStatement(deleteFactionInvitesQuery)) {
                stmt.setString(1, factionId);
                stmt.executeQuery();
            }

            // Deleting the faction
            String factionName;
            String deleteFactionQuery = "DELETE FROM faction WHERE id = ? RETURNING name";
            try (PreparedStatement stmt = connection.prepareStatement(deleteFactionQuery)) {
                stmt.setString(1, factionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("deleteFactionQuery query expected a result");
                    }
                    factionName = rs.getString("name");
                }
            }

            connection.commit();
            return factionName;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }  finally {
            connection.setAutoCommit(true);
        }
    }
}