package daveiiii.simpleFactions.data.namedQueries.factionInvite.update;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UpdatePlayerInviteTimestamp extends BaseQuery {
    public static void run (Connection connection, String factionId, UUID playerId) throws SQLException {
        String query = "UPDATE faction_invite SET timestamp = ? WHERE player_id = ? AND faction_id = ? RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, System.currentTimeMillis());
            stmt.setString(1, String.valueOf(playerId));
            stmt.setString(2, String.valueOf(factionId));
            stmt.executeQuery();
        }
    }
}
