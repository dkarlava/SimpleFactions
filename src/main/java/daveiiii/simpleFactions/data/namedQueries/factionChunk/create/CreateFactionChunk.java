package daveiiii.simpleFactions.data.namedQueries.factionChunk.create;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateFactionChunk extends BaseQuery {
    public static void run (Connection connection, String factionId, int x, int z) throws SQLException {
        String query = "INSERT INTO faction_chunk (faction_id, x, z) VALUES (?, ?, ?) RETURNING id;";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(factionId));
            stmt.setInt(2, x);
            stmt.setInt(3, z);
            stmt.executeQuery();
        }
    }
}