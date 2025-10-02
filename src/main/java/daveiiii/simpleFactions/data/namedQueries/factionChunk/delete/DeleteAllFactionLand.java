package daveiiii.simpleFactions.data.namedQueries.factionChunk.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllFactionLand {
    public static void run (Connection connection, String factionId) throws SQLException {
        String query = """
            DELETE FROM faction_chunk WHERE faction_id = ? RETURNING id;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);
            stmt.executeQuery();
        }
    }
}
