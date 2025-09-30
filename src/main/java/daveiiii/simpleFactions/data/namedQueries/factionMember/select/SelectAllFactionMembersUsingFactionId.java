package daveiiii.simpleFactions.data.namedQueries.factionMember.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.FactionPlayer;
import types.PlayerRank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelectAllFactionMembersUsingFactionId extends BaseQuery {
    public static List<FactionPlayer> run (Connection connection, String factionId) throws SQLException {
        String[] queryItems = {
                "SELECT player_id, rank",
                "FROM faction_member",
                "WHERE faction_id = ?",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<FactionPlayer> ret = new ArrayList<>();
                while (rs.next()) {
                    UUID playerId = UUID.fromString(rs.getString("player_id"));
                    ret.add(new FactionPlayer(PlayerRank.getValue(rs.getString("rank")), factionId, playerId));
                }
                return ret;
            }
        }
    }
}