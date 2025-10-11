package daveiiii.simpleFactions.data.namedQueries.factionMember.select;

import types.FactionPlayer;
import types.PlayerRank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelectAllFactionMembersUsingFactionId {
    public static List<FactionPlayer> run (Connection connection, String factionId) throws SQLException {
        String query = """
            SELECT fm.player_id AS playerId, fm.rank, pd.power, f.name
            FROM faction_member fm
            JOIN player_data pd ON fm.player_id = pd.player_id
            JOIN faction f ON fm.faction_id = f.id
            WHERE faction_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<FactionPlayer> ret = new ArrayList<>();
                while (rs.next()) {
                    UUID playerId = UUID.fromString(rs.getString("playerId"));
                    ret.add(new FactionPlayer(
                        PlayerRank.getValue(rs.getString("rank")),
                        factionId,
                        playerId,
                        rs.getInt("power"),
                        rs.getString("name")
                    ));
                }
                return ret;
            }
        }
    }
}