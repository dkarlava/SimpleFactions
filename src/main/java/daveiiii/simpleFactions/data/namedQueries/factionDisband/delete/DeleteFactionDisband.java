package daveiiii.simpleFactions.data.namedQueries.factionDisband.delete;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteFactionDisband extends BaseQuery {
    public static String run (Connection connection, String factionDisbandId) throws SQLException {
        try {
            connection.setAutoCommit(false);

            // Getting faction id
            String getFactionIdQuery = "SELECT faction_id FROM faction_disband WHERE id = ?";
            String factionId;
            try (PreparedStatement stmt = connection.prepareStatement(getFactionIdQuery)) {
                stmt.setString(1, factionDisbandId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("getFactionIdQuery query expected a result");
                    }
                    factionId = rs.getString("faction_id");
                }
            }

            // Deleting all members of the faction
            String getFactionQuery = "DELETE FROM faction_member WHERE faction_id = ? RETURNING id";
            try (PreparedStatement stmt = connection.prepareStatement(getFactionQuery)) {
                stmt.setString(1, factionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("getFactionIdQuery query expected a result");
                    }
                }
            }

            // Deleting faction disband record
            String deleteFactionDisbandQuery = "DELETE FROM faction_disband WHERE id = ? RETURNING id";
            try (PreparedStatement stmt = connection.prepareStatement(deleteFactionDisbandQuery)) {
                stmt.setString(1, factionDisbandId);
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