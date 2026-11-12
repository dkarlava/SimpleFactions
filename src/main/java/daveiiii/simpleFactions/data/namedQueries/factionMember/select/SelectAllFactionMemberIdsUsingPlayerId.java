package daveiiii.simpleFactions.data.namedQueries.factionMember.select;

import types.FactionPlayer;
import types.PlayerRank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelectAllFactionMemberIdsUsingPlayerId {
    public static List<UUID> run (Connection connection, UUID playerId) throws SQLException {
        String getFactionIdQuery = """
            SELECT faction_id
            FROM faction_member
            WHERE player_id = ?
        """;
        String queryFactionMemberIds = """
            SELECT player_id
            FROM faction_member
            WHERE faction_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(getFactionIdQuery)) {
            stmt.setString(1, String.valueOf(playerId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String factionId = rs.getString("faction_id");
                try (PreparedStatement stmt2 = connection.prepareStatement(queryFactionMemberIds)) {
                    stmt2.setString(1, factionId);
                    try (ResultSet rs2 = stmt2.executeQuery()) {
                        List<UUID> ret = new ArrayList<>();
                        while (rs2.next()) {
                            UUID factionMemberId = UUID.fromString(rs2.getString("player_id"));
                            ret.add(factionMemberId);
                        }
                        return ret;
                    }
                }
            }
        }
    }
}