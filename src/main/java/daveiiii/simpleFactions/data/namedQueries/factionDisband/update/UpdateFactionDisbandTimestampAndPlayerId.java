package daveiiii.simpleFactions.data.namedQueries.factionDisband.update;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UpdateFactionDisbandTimestampAndPlayerId  extends BaseQuery {
    public static void run (Connection connection, String factionDisbandId, UUID playerId) throws SQLException {
        String[] queryItems = {
                "UPDATE faction_disband",
                "SET",
                "player_id = ?,",
                "timestamp = ?",
                "WHERE id = ?",
                "RETURNING id"
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            stmt.setLong(2, System.currentTimeMillis());
            stmt.setString(3, String.valueOf(factionDisbandId));
            stmt.executeQuery();
        }
    }
}