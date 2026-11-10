package daveiiii.simpleFactions.data.namedQueries.playerData.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UpdatePlayerDataAutoMap {
    public static void run (Connection connection, UUID playerId, boolean autoMap) throws SQLException {
        String query = """
            UPDATE player_data SET auto_map = ? WHERE player_id = ? RETURNING ID;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, autoMap);
            stmt.setString(2, String.valueOf(playerId));
            stmt.executeQuery();
        }
    }
}

