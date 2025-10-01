package daveiiii.simpleFactions.data.namedQueries.factionChunk.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateFactionSafeZoneWarZoneChunk {
    public static void run (Connection connection, String factionName, int x, int z) throws SQLException {
        try {
            connection.setAutoCommit(false);
            String getSafezoneWarzoneFactionIdQuery = """
                    SELECT id FROM faction WHERE name_unique = ?
            """;
            String safezoneWarzoneFactionId;
            try (PreparedStatement stmt = connection.prepareStatement(getSafezoneWarzoneFactionIdQuery)) {
                stmt.setString(1, factionName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("getSafezoneWarzoneFactionIdQuery query expected a result");
                    }
                    safezoneWarzoneFactionId = rs.getString("id");
                }
            }

            String query = """
                UPDATE faction_chunk SET faction_id = ? WHERE x = ? AND z = ? RETURNING id;
            """;
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, String.valueOf(safezoneWarzoneFactionId));
                stmt.setInt(2, x);
                stmt.setInt(3, z);
                stmt.executeQuery();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }  finally {
            connection.setAutoCommit(true);
        }
    }
}
