package daveiiii.simpleFactions.data.namedQueries.factionMember.select;

import types.FactionPlayer;
import types.PlayerRank;
import java.sql.*;
import java.util.UUID;

public class SelectFactionMember {
    public static FactionPlayer run (Connection connection, UUID playerId) throws SQLException {
        String query = """
            SELECT fm.rank, fm.faction_id, pd.power
            FROM faction_member fm
            JOIN player_data pd ON fm.player_id = pd.player_id
            WHERE fm.player_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new FactionPlayer(PlayerRank.getValue(rs.getString("rank")), rs.getString("faction_id"), playerId, rs.getInt("power"));
            }
        }
    }
}