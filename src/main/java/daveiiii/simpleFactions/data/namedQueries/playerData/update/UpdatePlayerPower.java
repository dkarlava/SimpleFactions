package daveiiii.simpleFactions.data.namedQueries.playerData.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UpdatePlayerPower {
    public static void run (Connection connection, UUID playerId, int newPower) throws SQLException {
        String query = """
            UPDATE player_data SET power = ? WHERE player_id = ? RETURNING ID;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newPower);
            stmt.setString(2, String.valueOf(playerId));
            stmt.executeQuery();
        }
    }
}
