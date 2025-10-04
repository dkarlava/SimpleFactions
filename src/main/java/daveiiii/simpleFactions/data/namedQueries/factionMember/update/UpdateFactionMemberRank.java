package daveiiii.simpleFactions.data.namedQueries.factionMember.update;

import types.PlayerRank;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UpdateFactionMemberRank {
    public static void run (Connection connection, UUID playerId, PlayerRank rank) throws SQLException {
        String query = """
            UPDATE faction_member
            SET rank = ?
            WHERE player_id = ?
            RETURNING id;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, rank.getRank());
            stmt.setString(2, String.valueOf(playerId));
            stmt.executeQuery();
        }
    }
}