package daveiiii.simpleFactions.data.namedQueries.factionInvite.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.FactionDisband;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectFactionInvitePrimed extends BaseQuery {
    public static FactionDisband run (Connection connection, String factionId) throws SQLException {
        String[] queryItems = {
                "SELECT id, timestamp, player_id",
                "FROM faction_invite",
                "WHERE faction_id = ?",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(factionId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new FactionDisband(rs.getString("player_id"), rs.getLong("timestamp"), rs.getString("id"));
                }
                return null;
            }
        }
    }
}