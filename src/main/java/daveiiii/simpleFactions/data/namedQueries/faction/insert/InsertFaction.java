package daveiiii.simpleFactions.data.namedQueries.faction.insert;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.*;
import java.util.UUID;

public class InsertFaction extends BaseQuery {
    public static void run (Connection connection, String factionName, UUID playerId) throws SQLException {
        try {
            connection.setAutoCommit(false);

            // Inserting the faction
            String createFactionQuery = "INSERT INTO faction(name) VALUES(?) RETURNING id";
            String factionId;
            try (PreparedStatement stmt = connection.prepareStatement(createFactionQuery)) {
                stmt.setString(1, factionName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("CreateFactionQuery query expected a result");
                    }
                    factionId = rs.getString("id");
                }
            }

            // Inserting the player who call /f create as the owner
            String createFactionOwnerQuery = "INSERT INTO faction_member(player_id, faction_id, rank) VALUES(?, ?, ?) RETURNING id";
            try (PreparedStatement insertStmt = connection.prepareStatement(createFactionOwnerQuery)) {
                insertStmt.setString(1, String.valueOf(playerId));
                insertStmt.setString(2, factionId);
                insertStmt.setString(3, "owner");
                insertStmt.executeQuery();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }  finally {
            connection.setAutoCommit(true);
        }
    }
}
