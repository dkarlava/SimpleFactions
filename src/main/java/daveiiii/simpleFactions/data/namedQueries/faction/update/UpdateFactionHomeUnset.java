package daveiiii.simpleFactions.data.namedQueries.faction.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateFactionHomeUnset {
    public static void run (Connection connection, String factionId) throws SQLException {
        String query = """
            UPDATE faction
            SET home_x = NULL, home_y = NULL, home_z = NULL, home_yaw = NULL, home_pitch = NULL
            WHERE id = ?
            RETURNING id;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);
            stmt.executeQuery();
        }
    }
}