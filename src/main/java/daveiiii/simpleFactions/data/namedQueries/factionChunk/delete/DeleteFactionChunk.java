package daveiiii.simpleFactions.data.namedQueries.factionChunk.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteFactionChunk {
    public static void run (Connection connection, int x, int z) throws SQLException {
        String query = """
            DELETE FROM faction_chunk WHERE x = ? AND z = ? RETURNING id;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, x);
            stmt.setInt(2, z);
            stmt.executeQuery();
        }
    }
}
