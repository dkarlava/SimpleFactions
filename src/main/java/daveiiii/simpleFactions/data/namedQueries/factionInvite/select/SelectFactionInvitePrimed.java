package daveiiii.simpleFactions.data.namedQueries.factionInvite.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.FactionInvite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SelectFactionInvitePrimed extends BaseQuery {
    public static FactionInvite run (Connection connection, String factionId, UUID playerId) throws SQLException {
        String[] queryItems = {
                "SELECT id, timestamp, player_id",
                "FROM faction_invite",
                "WHERE faction_id = ?",
                "AND player_id = ?"
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(factionId));
            stmt.setString(2, String.valueOf(playerId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new FactionInvite(rs.getString("player_id"), rs.getLong("timestamp"), rs.getString("id"));
                }
                return null;
            }
        }
    }
}