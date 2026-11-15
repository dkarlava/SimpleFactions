package daveiiii.simpleFactions.data.namedQueries.factionChunk.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DeleteFactionChunk {
    public static void run (Connection connection, int x, int z, UUID worldId) throws SQLException {
        String query = """
            DELETE FROM
                faction_chunk
            WHERE
                x = ?
                AND z = ?
                AND world_id = ?
            RETURNING id;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, x);
            stmt.setInt(2, z);
            stmt.setString(3, String.valueOf(worldId));
            stmt.executeQuery();
        }
    }
}
