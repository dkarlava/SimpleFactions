package daveiiii.simpleFactions.data.namedQueries.factionInvite.insert;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class InsertPlayerInvite extends BaseQuery {
    public static void run (Connection connection, String factionId, UUID playerId) throws SQLException {
        String query = "INSERT INTO faction_invite (player_id, faction_id, timestamp) VALUES (?, ?, ?) RETURNING id;";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            stmt.setString(2, String.valueOf(factionId));
            stmt.setLong(3, System.currentTimeMillis());
            stmt.executeQuery();
        }
    }
}