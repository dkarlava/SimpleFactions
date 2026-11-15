package daveiiii.simpleFactions.data.namedQueries.factionChunk.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UpdateFactionChunk {
    public static void run (Connection connection, String factionId, int x, int z, UUID worldId) throws SQLException {
        String query = """
            UPDATE
                faction_chunk SET faction_id = ?
                WHERE
                    x = ?
                    AND z = ?
                    AND world_id = ?
                RETURNING id;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(factionId));
            stmt.setInt(2, x);
            stmt.setInt(3, z);
            stmt.setString(4, String.valueOf(worldId));
            stmt.executeQuery();
        }
    }
}
