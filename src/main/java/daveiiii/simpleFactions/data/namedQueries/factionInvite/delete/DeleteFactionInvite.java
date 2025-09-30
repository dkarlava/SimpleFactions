package daveiiii.simpleFactions.data.namedQueries.factionInvite.delete;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DeleteFactionInvite extends BaseQuery {
    public static void run (Connection connection, UUID playerId, String factionId) throws SQLException {
        String query = "DELETE FROM faction_invite WHERE player_id = ? AND faction_id = ? RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            stmt.setString(2, String.valueOf(factionId));
            stmt.executeQuery();
        }
    }
}