package daveiiii.simpleFactions.data.namedQueries.factionChunk.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllFactionLand {
    public static void run (Connection connection, String factionId) throws SQLException {
        String deleteAllLandQuery = """
            DELETE FROM faction_chunk WHERE faction_id = ? RETURNING id;
        """;

        String removeFactionHomeQuery = """
            UPDATE faction
            SET home_x = NULL, home_y = NULL, home_z = NULL, home_yaw = NULL, home_pitch = NULL
            WHERE id = ?
            RETURNING id;
        """;

        try (PreparedStatement stmt = connection.prepareStatement(deleteAllLandQuery)) {
            stmt.setString(1, factionId);
            stmt.executeQuery();
        }

        try (PreparedStatement stmt = connection.prepareStatement(removeFactionHomeQuery)) {
            stmt.setString(1, factionId);
            stmt.executeQuery();
        }
    }
}
