package daveiiii.simpleFactions.data.namedQueries.faction.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateFactionHome {
    public static void run (Connection connection, String factionId, double x, double y, double z, float yaw, float pitch) throws SQLException {
        String query = """
            UPDATE faction
            SET home_x = ?, home_y = ?, home_z = ?, home_yaw = ?, home_pitch = ?
            WHERE id = ?
            RETURNING id;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, x);
            stmt.setDouble(2, y);
            stmt.setDouble(3, z);
            stmt.setFloat(4, yaw);
            stmt.setFloat(5, pitch);
            stmt.setString(6, factionId);
            stmt.executeQuery();
        }
    }
}