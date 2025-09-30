package daveiiii.simpleFactions.data.namedQueries.playerData.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SelectPlayerDataAutoMap extends BaseQuery {
    public static boolean run (Connection connection, UUID playerId) throws SQLException {
        String query = """
            SELECT auto_map
            FROM player_data
            WHERE player_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Player data auto-map not found");
                }
                return rs.getBoolean("auto_map");
            }
        }
    }
}