package daveiiii.simpleFactions.data.namedQueries.factionMember.insert;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.PlayerRank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class InsertFactionMember extends BaseQuery {
    // NOTE: not including rank as a parameter since the act of invited a member defaults to 'Member' rank anyway
    public static void run (Connection connection, String factionId, UUID playerId) throws SQLException {
        String query = "INSERT INTO faction_member (player_id, faction_id, rank) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            stmt.setString(2, String.valueOf(factionId));
            stmt.setString(3, PlayerRank.Member.getRank());
            stmt.executeQuery();
        }
    }
}