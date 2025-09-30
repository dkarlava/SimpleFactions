package daveiiii.simpleFactions.data.namedQueries.factionMember.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.FactionPlayer;
import types.PlayerRank;

import java.sql.*;
import java.util.UUID;

public class SelectFactionPlayerRank extends BaseQuery {
    public static FactionPlayer run (Connection connection, UUID playerId) throws SQLException {
        String[] queryItems = {
                "SELECT rank, faction_id",
                "FROM faction_member",
                "WHERE player_id = ?",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("No faction player rank found for player " + playerId);
                }
                return new FactionPlayer(PlayerRank.getValue(rs.getString("rank")), rs.getString("faction_id"));
            }
        }
    }
}