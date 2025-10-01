package daveiiii.simpleFactions.data.namedQueries.factionChunk.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateFactionChunk {
    public static void run (Connection connection, String factionId, int x, int z) throws SQLException {
        String query = """
            UPDATE faction_chunk SET faction_id = ? WHERE x = ? AND z = ? RETURNING id;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(factionId));
            stmt.setInt(2, x);
            stmt.setInt(3, z);
            stmt.executeQuery();
        }
    }
}
